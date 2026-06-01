package com.recrutement.offer.service;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.service.CandidateService;
import com.recrutement.config.NotificationHelper;
import com.recrutement.history.service.ActionHistoryService;
import com.recrutement.offer.model.Offer;
import com.recrutement.offer.repository.OfferRepository;
import com.recrutement.recruitment.model.Application;
import com.recrutement.offer.model.OfferStatus;
import com.recrutement.recruitment.model.ApplicationStatus;
import com.recrutement.recruitment.model.Job;
import com.recrutement.recruitment.model.RecruitmentStep;
import com.recrutement.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final CandidateService candidateService;
    private final RecruitmentService recruitmentService;
    private final ActionHistoryService actionHistoryService;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationHelper notificationHelper;

    public List<Offer> getAll() {
        return offerRepository.findAll();
    }

    public Offer getById(Long id) {
        return offerRepository.findById(id).orElseThrow(() -> new RuntimeException("Offre non trouvée"));
    }

    public Offer generate(Long applicationId, Double salary, LocalDate startDate, String actor) {
        Application app = recruitmentService.getApplicationById(applicationId);
        Candidate candidate = candidateService.getCandidateById(app.getCandidateId());
        Job job = recruitmentService.getJobById(app.getJobId());

        String letter = String.format(
                "Madame, Monsieur %s %s,\n\nNous avons le plaisir de vous proposer le poste de %s pour un salaire annuel brut de %.0f €, avec une date de début prévue le %s.\n\nCordialement,\nService RH",
                candidate.getFirstName(), candidate.getLastName(), job.getTitle(),
                salary != null ? salary : 0, startDate != null ? startDate : LocalDate.now().plusMonths(1));

        Offer offer = Offer.builder()
                .candidateId(app.getCandidateId())
                .applicationId(applicationId)
                .salaryOffer(salary)
                .positionTitle(job.getTitle())
                .offerLetterContent(letter)
                .status(OfferStatus.EN_ATTENTE.name())
                .expirationDate(LocalDate.now().plusDays(14))
                .build();

        Offer saved = offerRepository.save(offer);
        recruitmentService.updateStep(applicationId, RecruitmentStep.OFFRE_EMBAUCHE, actor);
        actionHistoryService.record(app.getCandidateId(), applicationId, "OFFER_GENERATED", actor, "Offre générée");
        return saved;
    }

    public Offer send(Long id, String actor) {
        Offer offer = getById(id);
        offer.setStatus(OfferStatus.ENVOYEE.name());
        offer.setSentDate(LocalDate.now());
        Offer saved = offerRepository.save(offer);

        Candidate candidate = candidateService.getCandidateById(offer.getCandidateId());
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", candidate.getEmail());
        payload.put("recipientName", candidate.getFirstName() + " " + candidate.getLastName());
        payload.put("subject", "[PFA Recrutement] Offre d'embauche - " + offer.getPositionTitle());
        payload.put("message", offer.getOfferLetterContent());
        payload.put("type", "EMAIL");
        rabbitTemplate.convertAndSend("notification.exchange", "notification.key", payload);

        notificationHelper.notifyCandidateUser(candidate,
                "Offre d'embauche reçue",
                "Une offre d'embauche pour le poste « " + offer.getPositionTitle()
                        + " » vous a été envoyée. Consultez la page Offres d'embauche pour répondre.",
                "OFFER");

        actionHistoryService.record(offer.getCandidateId(), offer.getApplicationId(), "OFFER_SENT", actor, "Offre envoyée par email");
        return saved;
    }

    public Offer respond(Long id, boolean accepted, String actor) {
        Offer offer = getById(id);
        offer.setStatus(accepted ? OfferStatus.ACCEPTEE.name() : OfferStatus.REFUSEE.name());
        Offer saved = offerRepository.save(offer);

        Application app = recruitmentService.getApplicationById(offer.getApplicationId());
        recruitmentService.updateStatusInternal(app.getId(),
                accepted ? ApplicationStatus.EMBAUCHE : ApplicationStatus.REJETE, actor);
        if (accepted) {
            recruitmentService.calculerNoteGlobale(app.getId());
        }

        Candidate candidate = candidateService.getCandidateById(offer.getCandidateId());
        String notifTitle = accepted ? "Offre acceptée" : "Offre refusée";
        String notifBody = accepted
                ? "Vous avez accepté l'offre pour le poste « " + offer.getPositionTitle() + " ». Félicitations !"
                : "Vous avez refusé l'offre pour le poste « " + offer.getPositionTitle() + " ».";
        notificationHelper.notifyCandidateUser(candidate, notifTitle, notifBody, "OFFER");

        actionHistoryService.record(offer.getCandidateId(), offer.getApplicationId(),
                accepted ? "OFFER_ACCEPTED" : "OFFER_REJECTED", actor,
                accepted ? "Offre acceptée" : "Offre refusée");
        return saved;
    }
}
