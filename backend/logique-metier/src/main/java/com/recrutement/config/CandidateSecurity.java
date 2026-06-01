package com.recrutement.config;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("candidateSecurity")
@RequiredArgsConstructor
public class CandidateSecurity {

    private final CandidateService candidateService;

    public boolean isOwner(Long id, String username) {
        if (id == null || username == null) {
            return false;
        }
        try {
            Candidate candidate = candidateService.getCandidateById(id);
            return username.equals(candidate.getUsername());
        } catch (Exception e) {
            return false;
        }
    }
}
