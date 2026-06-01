package com.recrutement.deptreview.service;

import com.recrutement.history.service.ActionHistoryService;
import com.recrutement.inapp.service.AppNotificationService;
import com.recrutement.messaging.service.MessageService;
import com.recrutement.recruitment.model.Application;
import com.recrutement.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeptReviewService {

    private static final String RECRUITER_USERNAME = "recruteur";

    private final RecruitmentService recruitmentService;
    private final ActionHistoryService actionHistoryService;
    private final AppNotificationService appNotificationService;
    private final MessageService messageService;

    @Transactional
    public Map<String, Object> submitReview(Long applicationId, String actor, String decision,
                                            int techRating, int commRating, int fitRating, String comment) {
        Application app = recruitmentService.getApplicationById(applicationId);
        String action = "avancer".equalsIgnoreCase(decision) ? "Recommandation AVANCER" : "Recommandation REJETER";
        String scores = String.format("Technique %d/5, Communication %d/5, Culture Fit %d/5",
                techRating, commRating, fitRating);
        String details = String.format("%s — %s. Commentaire : %s",
                action, scores, comment != null && !comment.isBlank() ? comment : "Aucun commentaire.");

        actionHistoryService.record(app.getCandidateId(), applicationId, "DEPT_REVIEW", actor, details);

        String title = "[Resp. département] Avis sur candidature #" + applicationId;
        String body = details;
        appNotificationService.create(RECRUITER_USERNAME, title, body, "DEPT_REVIEW");

        String msg = String.format("[Resp. département] %s — candidature #%d\n%s\nCommentaire : %s",
                action, applicationId, scores,
                comment != null && !comment.isBlank() ? comment : "Aucun commentaire.");
        messageService.send(actor, RECRUITER_USERNAME, msg);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("applicationId", applicationId);
        result.put("message", "Avis transmis au recruteur");
        return result;
    }

    public List<Map<String, Object>> getRecentForRecruiter() {
        return actionHistoryService.getRecentByType("DEPT_REVIEW", 20).stream()
                .map(h -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("applicationId", h.getApplicationId());
                    row.put("candidateId", h.getCandidateId());
                    row.put("actorUsername", h.getActorUsername());
                    row.put("details", h.getDetails());
                    row.put("createdAt", h.getCreatedAt());
                    return row;
                })
                .collect(Collectors.toList());
    }
}
