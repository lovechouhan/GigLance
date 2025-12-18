package com.minor.freelancing.Controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Repositories.ProjectRepository;
import com.minor.freelancing.Services.GeminiService;
import com.minor.freelancing.Services.ProjectService;

@RestController
@RequestMapping("/api/ai-summary")
public class ProductAIController {

    private static final Logger log = LoggerFactory.getLogger(ProductAIController.class);

    @Autowired
    private GeminiService geminiService;
  
    @Autowired
    private ProjectService projectService;

    @PostMapping("/{id}")
    public Map<String, String> generateProductSummary(@PathVariable Long id, Model model) {
        Projects projects = projectService.findById(id);

        
        String name = projects != null && projects.getTitle() != null ? projects.getTitle() : "Untitled Project";
        String projectDescription = projects != null && projects.getDescription() != null ? projects.getDescription() : "";
        String projStatus = projects != null && projects.getStatus() != null ? projects.getStatus() : "UNKNOWN";
        String budgetStr = projects != null && projects.getBudget() != null ? String.format("%.2f", projects.getBudget()) : "not specified";
        String cat = projects != null && projects.getCategory() != null ? projects.getCategory() : "Uncategorized";
        // LocalDate deadline = projects != null ? projects.getDeadline() : null;
        // String deadlineStr = deadline != null ? deadline.toString() : "no deadline";
        String progressStr = projects != null && projects.getProgress() != null ? projects.getProgress().toString() : "0";
        String createdAtStr = projects != null && projects.getCreatedAt() != null ? projects.getCreatedAt().toString() : "";
        String clientName = projects != null && projects.getClient() != null ? projects.getClient().getName() : "";
        // String freelancerName = projects != null && projects.getFreelancer() != null ? projects.getFreelancer().getName() : "";
        // String deliveryDateStr = projects != null && projects.getDeliveryDate() != null ? projects.getDeliveryDate().toString() : "";

        // Fallback variables used in existing catch block
        
        String reviews = "";

        // Prompts for AI service (text blocks for readability)
        String summaryPrompt = """
            Provide a concise (2-3 sentence) summary for the following project:
            Title: %s
            Category: %s
            Status: %s
            Budget: %s
            Progress: %s%%
            Client: %s
            Short description: %s
            Created at: %s
            """.formatted(name, cat, projStatus, budgetStr, progressStr, clientName, projectDescription, createdAtStr);

       String descriptionPrompt = """
            Create a short, crisp, freelancer-friendly project insight based on the following details.
            Summarize only the most important points. Keep the tone helpful and professional.

            Include:
            - What the client needs
            - Key objectives and deliverables
            - Technical or domain expectations (based on category)
            - Expected timeline urgency (based on created date)
            - Budget clarity
            - Project progress/status
            - Any additional considerations a freelancer must know before bidding

            Here are the project details:

            Title: %s
            Category: %s
            Description: %s
            Budget: %s
            Status: %s
            Progress: %s%%
            Client Name: %s
            Created At: %s

            Generate:
            1️⃣ A short 2–3 line AI Summary
            2️⃣ A 3–5 line Detailed Insight highlighting scope, expectations, and risks.
            3️⃣ Do not Use the Symbol of $ in the description.
            4️⃣ Always put Our Client before any mention of the client.
            5️⃣ highlight these projects details in a small table format.

            Keep everything concise and valuable for a freelancer preparing a proposal.
            """.formatted(
                    name,
                    cat,
                    projectDescription,
                    budgetStr,
                    projStatus,
                    progressStr,
                    clientName,
                    createdAtStr
            );


        // expose prompts to model (optional, helpful for debugging or template rendering)
        model.addAttribute("aiSummaryPrompt", summaryPrompt);
        model.addAttribute("aiDescriptionPrompt", descriptionPrompt);

        String summary;
        String description;
        try {
            summary = geminiService.generateProjectSummary2(name, cat, projStatus, budgetStr, projectDescription, clientName, projects.getCreatedAt());
        } catch (Exception ex) {
            log.warn("AI summary generation failed for project {}: {}", id, ex.toString());
            summary = "AI service is currently unavailable. Showing a basic summary.\n" +
                    String.format("%s (%s). Reviews: %s", name, cat,
                            !reviews.isBlank() ? "available" : "not available");
        }

        try {
            description = geminiService.generateProjectDescription2(name, cat, projStatus, budgetStr, projectDescription, clientName, projects.getCreatedAt());
        } catch (Exception ex) {
            log.warn("AI description generation failed for project {}: {}", id, ex.toString());
            description = "AI description is unavailable. Please try again later.";
        }

        model.addAttribute("projects", projects);
        Map<String, String> response = new HashMap<>();
        response.put("summary", summary);
        response.put("description", description);
        return response;
    }
}
