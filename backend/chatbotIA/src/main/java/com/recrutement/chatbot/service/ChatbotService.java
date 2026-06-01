package com.recrutement.chatbot.service;

import com.recrutement.chatbot.client.LogiqueMetierClient;
import com.recrutement.chatbot.dto.ChatMessageRequestDto;
import com.recrutement.chatbot.dto.ChatbotResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final LogiqueMetierClient logiqueMetierClient;

    public ChatbotResponseDto processMessage(ChatMessageRequestDto request) {
        String question = request.getMessage() != null ? request.getMessage().toLowerCase() : "";
        String userId = request.getUserId();

        if (matches(question, "statut", "candidature", "suivi", "avancement")) {
            return new ChatbotResponseDto(answerStatutCandidature(userId));
        }
        if (matches(question, "prochain", "entretien", "rendez-vous", "quand")) {
            return new ChatbotResponseDto(answerProchainEntretien(userId));
        }
        if (matches(question, "cv", "soumettre", "postuler", "candidature", "déposer")) {
            return new ChatbotResponseDto(
                    "Pour soumettre votre CV : connectez-vous, allez dans « Offres d'emploi », choisissez un poste et cliquez sur « Postuler ». "
                            + "Vous pouvez aussi mettre à jour votre profil dans « Mon profil » et joindre votre CV.");
        }
        if (matches(question, "étape", "etapes", "processus", "pipeline", "recrutement")) {
            return new ChatbotResponseDto(
                    "Les étapes du recrutement sont : 1) Pré-sélection 2) Entretien RH 3) Test technique 4) Entretien final 5) Offre d'embauche.");
        }
        if (matches(question, "contact", "recruteur", "joindre", "email")) {
            return new ChatbotResponseDto(
                    "Pour contacter un recruteur, utilisez la messagerie interne (menu « Messagerie ») ou écrivez à recruteur@recrutement.com.");
        }
        return new ChatbotResponseDto(
                "Je peux vous aider sur : le statut de votre candidature, votre prochain entretien, "
                        + "la soumission de CV, les étapes du recrutement ou le contact recruteur.");
    }

    public ChatbotResponseDto processQuestion(String question) {
        return processMessage(new ChatMessageRequestDto(null, question));
    }

    private boolean matches(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }

    private String answerStatutCandidature(String username) {
        if (username == null || username.isBlank()) {
            return "Connectez-vous pour consulter le statut de vos candidatures.";
        }
        Map<String, Object> candidate = logiqueMetierClient.getCandidateByUsername(username);
        if (candidate == null) {
            return "Profil candidat introuvable. Complétez votre profil dans « Mon profil ».";
        }
        Long id = ((Number) candidate.get("id")).longValue();
        List<Map<String, Object>> apps = logiqueMetierClient.getApplicationsByCandidate(id);
        if (apps.isEmpty()) {
            return "Vous n'avez pas encore de candidature enregistrée.";
        }
        StringBuilder sb = new StringBuilder("État de vos candidatures :\n");
        for (Map<String, Object> app : apps) {
            sb.append("- Poste #").append(app.get("jobId"))
                    .append(" : étape ").append(app.get("currentStep"))
                    .append(", statut ").append(app.get("status")).append("\n");
        }
        return sb.toString();
    }

    private String answerProchainEntretien(String username) {
        if (username == null || username.isBlank()) {
            return "Connectez-vous pour voir vos entretiens planifiés.";
        }
        Map<String, Object> candidate = logiqueMetierClient.getCandidateByUsername(username);
        if (candidate == null) {
            return "Profil candidat introuvable.";
        }
        Long id = ((Number) candidate.get("id")).longValue();
        List<Map<String, Object>> interviews = logiqueMetierClient.getInterviewsByCandidate(id);
        return interviews.stream()
                .filter(i -> "SCHEDULED".equals(i.get("status")))
                .min(Comparator.comparing(i -> String.valueOf(i.get("interviewDate"))))
                .map(i -> "Votre prochain entretien : " + i.get("interviewDate")
                        + " avec " + i.get("interviewerName")
                        + " (" + i.get("type") + ")")
                .orElse("Aucun entretien planifié pour le moment.");
    }
}
