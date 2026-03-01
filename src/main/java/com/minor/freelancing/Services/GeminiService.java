package com.minor.freelancing.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class GeminiService {

    
    // ⚠️ apni key lagao
    // private final String MODEL = "gemini-2.5-flash"; // ya gemini-1.5-flash

    @Value("${google.api.key}")
    private String API_KEY;

    @Value("${google.api.model}")
    private String MODEL;

    private final WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models")
                .build();
    }


    private String callGeminiWithRetry(Map<String,Object> body, int retries) {
    for (int i = 0; i < retries; i++) {
        try {
            return webClient.post()
                    .uri("/" + MODEL + ":generateContent?key=" + API_KEY)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            if (e.getMessage().contains("503")) {
                int delay = 500 * (i + 1);
                System.out.println("⚠️ Gemini overloaded. Retrying in " + delay + "ms...");
                try { Thread.sleep(delay); } catch (Exception ignored) {}
            } else {
                throw e;
            }
        }
    }
    throw new RuntimeException("Gemini failed after retries");
}



    // ✅ Common API call — returns only the text part
    private String callGeminiAPI(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))));

            String response = webClient.post()
                    .uri("/" + MODEL + ":generateContent?key=" + API_KEY)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // ✅ Extract only the AI text from the JSON
            JsonNode json = objectMapper.readTree(response);
            return json.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error while generating AI response: " + e.getMessage();
        }
    }

    // ✅ Short summary
    public String generateProjectSummary2(String name, String category, String status, String budget,
            String description, String clientName, LocalDateTime createdAt) {
        String prompt = String.format("""
                    Write a 1–2 line catchy summary for this project:
                    Name: %s
                    Category: %s
                    Status: %s
                    Budget: %s
                    Description: %s
                    Client: %s
                    Created at: %s
                """, name, category, status, budget, description, clientName, createdAt);
        return callGeminiAPI(prompt);
    }

    // ✅ Detailed description
    public String generateProjectDescription2(String name, String category, String status, String budget,
            String description, String clientName, LocalDateTime createdAt) {
        String prompt = String.format("""
                    Write a detailed and engaging 3–4 line description for this project:
                    Name: %s
                    Category: %s
                    Status: %s
                    Budget: %s
                    Description: %s
                    Client: %s
                    Created at: %s
                """, name, category, status, budget, description, clientName, createdAt);
        return callGeminiAPI(prompt);
    }

   public List<Map<String, Object>> generateTaskTitles2(String prompt) {

    try {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt))))
        );

        // 🔥 Automatic retry + WebClient stability
        String response = callGeminiWithRetry(body, 5);

        JsonNode root = objectMapper.readTree(response);

        JsonNode content = root.path("candidates").get(0)
                               .path("content").path("parts").get(0);

        String aiText = content.path("text").asText();

        String cleanJson = aiText.replace("```json", "")
                                 .replace("```", "")
                                 .trim();

        ArrayNode arr = (ArrayNode) objectMapper.readTree(cleanJson);

        List<Map<String, Object>> tasks = new ArrayList<>();

        for (JsonNode node : arr) {
            tasks.add(Map.of(
                    "title", node.get("title").asText(),
                    "weightage", node.get("weightage").asInt()
            ));
        }

        return tasks;

    } catch (Exception e) {
        e.printStackTrace();
        return List.of();
    }
}


    // genratig task with gemini
    public List<Map<String, Object>> generateTaskTitles(String projectTitle, String projectDescription,
            String userDescription) {

        String prompt = """
                You are an expert project planner.

                Break this project into a clean list of tasks and assign weightage for each task.
                Weightage MUST reflect task complexity and importance.
                Total weightage MUST be exactly 100.

                Project Title: %s
                Project Description: %s

                Additional Info:
                %s

                OUTPUT STRICTLY AND ONLY IN PURE JSON ARRAY:

                [
                  { "title": "Task 1", "weightage": 20 },
                  { "title": "Task 2", "weightage": 30 },
                  { "title": "Task 3", "weightage": 50 }
                ]

                Rules:
                - No text outside JSON.
                - Weightage must sum to exactly 100.
                - Weightage must be integers only.
                """.formatted(projectTitle, projectDescription, userDescription == null ? "" : userDescription);

        return generateTaskTitles2(prompt);
    }

}
