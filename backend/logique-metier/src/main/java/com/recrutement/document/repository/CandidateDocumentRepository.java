package com.recrutement.document.repository;

import com.recrutement.document.model.CandidateDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateDocumentRepository extends JpaRepository<CandidateDocument, Long> {
    List<CandidateDocument> findByCandidateId(Long candidateId);
    List<CandidateDocument> findByApplicationId(Long applicationId);
}
