package com.recrutement.recruitment.service;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.service.CandidateService;
import com.recrutement.history.service.ActionHistoryService;
import com.recrutement.config.NotificationHelper;
import com.recrutement.recruitment.model.Application;
import com.recrutement.recruitment.model.ApplicationStatus;
import com.recrutement.recruitment.model.Job;
import com.recrutement.recruitment.model.RecruitmentStep;
import com.recrutement.interview.model.Evaluation;
import com.recrutement.interview.repository.EvaluationRepository;
import com.recrutement.recruitment.repository.ApplicationRepository;
import com.recrutement.recruitment.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateService candidateService;
    private final RabbitTemplate rabbitTemplate;
    private final ActionHistoryService actionHistoryService;
    private final NotificationHelper notificationHelper;
    private final EvaluationRepository evaluationRepository;

    @PostConstruct
    public void initData() {
        migrateLegacySteps();
        if (jobRepository.count() == 0) {
            Job j1 = Job.builder()
                    .title("Développeur Java Senior (Microservices)")
                    .department("R&D / IT")
                    .description("Nous recherchons un développeur Java expert en Spring Boot...")
                    .location("Paris / Remote")
                    .status("OPEN")
                    .createdDate(LocalDate.now().minusDays(5))
                    .build();
            jobRepository.save(j1);

            Job j2 = Job.builder()
                    .title("Product Owner RH Tech")
                    .department("Produit")
                    .description("Gestion de notre roadmap de recrutement...")
                    .location("Lyon")
                    .status("OPEN")
                    .createdDate(LocalDate.now().minusDays(2))
                    .build();
            jobRepository.save(j2);

            if (candidateService.getAllCandidates().stream().findFirst().isPresent()) {
                Long candidateId = candidateService.getAllCandidates().get(0).getId();
                Application app1 = Application.builder()
                        .jobId(j1.getId())
                        .candidateId(candidateId)
                        .currentStep(RecruitmentStep.PRE_SELECTION)
                        .status(ApplicationStatus.EN_ATTENTE)
                        .appliedDate(LocalDate.now().minusDays(4))
                        .notes("Excellent profil technique")
                        .recruiterUsername("recruteur")
                        .archived(false)
                        .build();
                applicationRepository.save(app1);
            }
        }
    }

    private void migrateLegacySteps() {
        applicationRepository.findAll().forEach(app -> {
            RecruitmentStep step = app.getCurrentStep();
            if (step == null) {
                app.setCurrentStep(RecruitmentStep.PRE_SELECTION);
            } else if (step == RecruitmentStep.ENTRETIEN) {
                app.setCurrentStep(RecruitmentStep.ENTRETIEN_RH);
            } else if (step == RecruitmentStep.TEST) {
                app.setCurrentStep(RecruitmentStep.TEST_TECHNIQUE);
            } else if (step == RecruitmentStep.OFFRE) {
                app.setCurrentStep(RecruitmentStep.OFFRE_EMBAUCHE);
            } else {
                return;
            }
            applicationRepository.save(app);
        });
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Poste non trouvé"));
    }

    public Job createJob(Job job) {
        job.setCreatedDate(LocalDate.now());
        job.setStatus("OPEN");
        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job details) {
        Job job = getJobById(id);
        job.setTitle(details.getTitle());
        job.setDepartment(details.getDepartment());
        job.setDescription(details.getDescription());
        job.setLocation(details.getLocation());
        job.setStatus(details.getStatus());
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public List<Application> searchApplications(Long jobId, Long candidateId, RecruitmentStep step,
                                                 ApplicationStatus status, String recruiterUsername,
                                                 Boolean archived, LocalDate fromDate, LocalDate toDate) {
        if (step == RecruitmentStep.OFFRE_EMBAUCHE) {
            List<Application> merged = new java.util.ArrayList<>(applicationRepository.search(
                    jobId, candidateId, RecruitmentStep.OFFRE_EMBAUCHE, status, recruiterUsername, archived, fromDate, toDate));
            applicationRepository.search(jobId, candidateId, RecruitmentStep.OFFRE, status, recruiterUsername, archived, fromDate, toDate)
                    .stream()
                    .filter(a -> merged.stream().noneMatch(m -> m.getId().equals(a.getId())))
                    .forEach(merged::add);
            return merged;
        }
        return applicationRepository.search(jobId, candidateId, step, status, recruiterUsername, archived, fromDate, toDate);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
    }

    public List<Application> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public Application apply(Application application, String actor) {
        application.setAppliedDate(LocalDate.now());
        application.setCurrentStep(RecruitmentStep.PRE_SELECTION);
        application.setStatus(ApplicationStatus.EN_ATTENTE);
        application.setArchived(false);
        Application saved = applicationRepository.save(application);

        actionHistoryService.record(saved.getCandidateId(), saved.getId(), "APPLICATION_CREATED", actor,
                "Candidature soumise pour le poste " + saved.getJobId());
        sendNotificationEvent(saved, "Candidature reçue", "Votre candidature a été soumise avec succès.");
        notifyCandidate(saved, "Candidature enregistrée", "Votre candidature a bien été reçue.");

        return saved;
    }

    public Application updateStep(Long id, RecruitmentStep step, String actor) {
        Application application = getApplicationById(id);
        application.setCurrentStep(step);
        Application saved = applicationRepository.save(application);

        actionHistoryService.record(saved.getCandidateId(), saved.getId(), "STEP_CHANGED", actor,
                "Étape : " + step);
        sendNotificationEvent(saved, "Mise à jour d'étape", "Votre candidature est passée à l'étape : " + stepLabel(step));
        notifyCandidate(saved, "Étape mise à jour", "Votre dossier est à l'étape : " + stepLabel(step));

        return saved;
    }

    public Application updateStatus(Long id, ApplicationStatus status, String actor) {
        return updateStatusInternal(id, status, actor);
    }

    public Application updateStatusInternal(Long id, ApplicationStatus status, String actor) {
        Application application = getApplicationById(id);
        application.setStatus(status);
        if (status == ApplicationStatus.REJETE) {
            application.setArchived(true);
        } else if (status == ApplicationStatus.EMBAUCHE) {
            application.setArchived(false);
            application.setCurrentStep(RecruitmentStep.OFFRE_EMBAUCHE);
        }
        Application saved = applicationRepository.save(application);

        String message = switch (status) {
            case SELECTIONNE -> "Félicitations, vous avez été sélectionné !";
            case EMBAUCHE -> "Félicitations, votre embauche est confirmée !";
            case REJETE -> "Nous regrettons de vous informer que votre candidature n'a pas été retenue.";
            default -> "Le statut de votre candidature a été mis à jour.";
        };

        actionHistoryService.record(saved.getCandidateId(), saved.getId(), "STATUS_CHANGED", actor,
                "Statut : " + status);
        sendNotificationEvent(saved, "Mise à jour de statut", message);
        notifyCandidate(saved, "Statut mis à jour", message);

        return saved;
    }

    /** Moyenne des notes d'évaluation liées à la candidature (diagramme : calculerNoteGlobale). */
    public Application calculerNoteGlobale(Long applicationId) {
        Application application = getApplicationById(applicationId);
        List<Evaluation> evaluations = evaluationRepository.findAllByApplicationId(applicationId);
        double avg = evaluations.stream()
                .map(Evaluation::getGlobalScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
        double note = Math.round(avg * 10.0) / 10.0;
        application.setGlobalScore(note > 0 ? note : null);
        return applicationRepository.save(application);
    }

    private String stepLabel(RecruitmentStep step) {
        return switch (step) {
            case PRE_SELECTION -> "Pré-sélection";
            case ENTRETIEN_RH, ENTRETIEN -> "Entretien RH";
            case TEST_TECHNIQUE, TEST -> "Test technique";
            case ENTRETIEN_FINAL -> "Entretien final";
            case OFFRE, OFFRE_EMBAUCHE -> "Offre d'embauche";
        };
    }

    private void notifyCandidate(Application app, String title, String body) {
        try {
            Candidate c = candidateService.getCandidateById(app.getCandidateId());
            notificationHelper.notifyCandidateUser(c, title, body, "INFO");
        } catch (Exception e) {
            log.warn("Could not create in-app notification: {}", e.getMessage());
        }
    }

    private void sendNotificationEvent(Application app, String subject, String message) {
        try {
            Candidate candidate = candidateService.getCandidateById(app.getCandidateId());
            Job job = getJobById(app.getJobId());

            Map<String, Object> payload = new HashMap<>();
            payload.put("email", candidate.getEmail());
            payload.put("recipientName", candidate.getFirstName() + " " + candidate.getLastName());
            payload.put("subject", "[PFA Recrutement] " + subject + " - " + job.getTitle());
            payload.put("message", message);
            payload.put("type", "EMAIL");

            rabbitTemplate.convertAndSend("notification.exchange", "notification.key", payload);
            log.info("Notification event sent to RabbitMQ for candidate: {}", candidate.getEmail());
        } catch (Exception e) {
            log.error("Failed to send notification event: {}", e.getMessage());
        }
    }
}
