package com.recrutement.chatbot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class LogiqueMetierClient {

    private final RestClient restClient;

    public LogiqueMetierClient(@Value("${logique-metier.url:http://localhost:8083}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Map<String, Object> getCandidateByUsername(String username) {
        try {
            return restClient.get()
                    .uri("/api/candidates/user/{username}", username)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getApplicationsByCandidate(Long candidateId) {
        try {
            return restClient.get()
                    .uri("/api/recruitment/applications/candidate/{id}", candidateId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> getInterviewsByCandidate(Long candidateId) {
        try {
            return restClient.get()
                    .uri("/api/interviews/candidate/{id}", candidateId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
