package com.minor.freelancing.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;


@Service
public class JobSearchServices {


     private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> fetchLiveJobs() {

        String url = "https://remotive.com/api/remote-jobs";

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        return (List<Map<String, Object>>) response.get("jobs");
    }

}
