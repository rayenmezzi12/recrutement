package com.recrutement.history.service;

import com.recrutement.history.model.ActionHistory;
import com.recrutement.history.repository.ActionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionHistoryService {

    private final ActionHistoryRepository repository;

    public void record(Long candidateId, Long applicationId, String actionType, String actor, String details) {
        repository.save(ActionHistory.builder()
                .candidateId(candidateId)
                .applicationId(applicationId)
                .actionType(actionType)
                .actorUsername(actor != null ? actor : "system")
                .details(details)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<ActionHistory> getByCandidate(Long candidateId) {
        return repository.findByCandidateIdOrderByCreatedAtDesc(candidateId);
    }

    public List<ActionHistory> getByApplication(Long applicationId) {
        return repository.findByApplicationIdOrderByCreatedAtDesc(applicationId);
    }

    public List<ActionHistory> getRecentByType(String actionType, int limit) {
        return repository.findByActionTypeOrderByCreatedAtDesc(actionType).stream()
                .limit(limit)
                .toList();
    }
}
