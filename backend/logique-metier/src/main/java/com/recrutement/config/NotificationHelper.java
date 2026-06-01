package com.recrutement.config;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.inapp.service.AppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final AppNotificationService appNotificationService;

    public void notifyCandidateUser(Candidate candidate, String title, String body, String type) {
        String username = resolveUsername(candidate);
        if (username != null) {
            appNotificationService.create(username, title, body, type != null ? type : "INFO");
        }
    }

    public static String resolveUsername(Candidate candidate) {
        if (candidate == null) {
            return null;
        }
        if (candidate.getUsername() != null && !candidate.getUsername().isBlank()) {
            return candidate.getUsername().trim();
        }
        if (candidate.getEmail() != null && candidate.getEmail().contains("@")) {
            return candidate.getEmail().substring(0, candidate.getEmail().indexOf('@')).trim();
        }
        return null;
    }
}
