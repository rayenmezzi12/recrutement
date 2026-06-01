package com.recrutement.interview.service;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.service.CandidateService;
import com.recrutement.history.service.ActionHistoryService;
import com.recrutement.interview.model.Evaluation;
import com.recrutement.interview.model.Interview;
import com.recrutement.interview.repository.EvaluationRepository;
import com.recrutement.interview.repository.InterviewRepository;
import com.recrutement.recruitment.service.RecruitmentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final EvaluationRepository evaluationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CandidateService candidateService;
    private final ActionHistoryService actionHistoryService;
    private final RecruitmentService recruitmentService;

    @PostConstruct
    public void initData() {
        if (interviewRepository.count() == 0 && !candidateService.getAllCandidates().isEmpty()) {
            Long candidateId = candidateService.getAllCandidates().get(0).getId();
            Interview i1 = Interview.builder()
                    .candidateId(candidateId)
                    .applicationId(1L)
                    .interviewerName("John Doe (RH)")
                    .interviewDate(LocalDateTime.now().plusDays(2))
                    .type("ONLINE")
                    .location("Microsoft Teams")
                    .status("SCHEDULED")
                    .feedback("Entretien RH")
                    .build();
            interviewRepository.save(i1);
        }
    }

    public List<Interview> getAllInterviews() {
        return interviewRepository.findAll();
    }

    public Interview getInterviewById(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entretien non trouvé"));
    }

    public List<Interview> getInterviewsByCandidate(Long candidateId) {
        return interviewRepository.findByCandidateId(candidateId);
    }

    public List<Interview> getInterviewsByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId);
    }

    public Optional<Interview> getNextScheduledForCandidate(Long candidateId) {
        return interviewRepository.findByCandidateId(candidateId).stream()
                .filter(i -> "SCHEDULED".equals(i.getStatus()))
                .filter(i -> i.getInterviewDate() != null && i.getInterviewDate().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Interview::getInterviewDate));
    }

    public Interview scheduleInterview(Interview interview) {
        interview.setStatus("SCHEDULED");
        Interview saved = interviewRepository.save(interview);

        actionHistoryService.record(saved.getCandidateId(), saved.getApplicationId(), "INTERVIEW_SCHEDULED", "system",
                "Entretien planifié le " + saved.getInterviewDate());

        String msg = "Votre entretien est planifié le " + saved.getInterviewDate()
                + " avec " + saved.getInterviewerName()
                + " (" + saved.getType() + ")"
                + (saved.getLocation() != null ? " — Lieu : " + saved.getLocation() : "");

        sendInterviewNotification(saved, "Entretien planifié", msg, "EMAIL");
        sendInterviewNotification(saved, "Rappel entretien", "Rappel SMS : " + msg, "SMS");

        return saved;
    }

    public Interview updateInterviewStatus(Long id, String status) {
        Interview interview = getInterviewById(id);
        interview.setStatus(status);
        Interview saved = interviewRepository.save(interview);

        sendInterviewNotification(saved, "Statut entretien mis à jour",
                "Le statut de votre entretien est : " + status + ".", "EMAIL");

        return saved;
    }

    public Evaluation submitEvaluation(Evaluation evaluation) {
        Interview interview = getInterviewById(evaluation.getInterviewId());
        interview.setStatus("COMPLETED");
        interviewRepository.save(interview);

        double tech = evaluation.getTechnicalRating() != null ? evaluation.getTechnicalRating() : 0;
        double comm = evaluation.getCommunicationRating() != null ? evaluation.getCommunicationRating() : 0;
        evaluation.setGlobalScore(Math.round((tech * 0.6 + comm * 0.4) * 10.0) / 10.0);

        Evaluation saved = evaluationRepository.save(evaluation);
        recruitmentService.calculerNoteGlobale(interview.getApplicationId());
        actionHistoryService.record(interview.getCandidateId(), interview.getApplicationId(), "EVALUATION_SUBMITTED",
                "system", "Note globale : " + saved.getGlobalScore() + "/5");
        return saved;
    }

    public Evaluation getEvaluationByInterview(Long interviewId) {
        return evaluationRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Aucune évaluation trouvée pour cet entretien"));
    }

    private void sendInterviewNotification(Interview interview, String subject, String message, String type) {
        try {
            Candidate candidate = candidateService.getCandidateById(interview.getCandidateId());
            Map<String, Object> payload = new HashMap<>();
            payload.put("email", candidate.getEmail());
            payload.put("recipientName", candidate.getFirstName() + " " + candidate.getLastName());
            payload.put("subject", "[Entretien] " + subject);
            payload.put("message", message);
            payload.put("type", type);
            rabbitTemplate.convertAndSend("notification.exchange", "notification.key", payload);
        } catch (Exception e) {
            log.error("Failed to send interview notification: {}", e.getMessage());
        }
    }
}
