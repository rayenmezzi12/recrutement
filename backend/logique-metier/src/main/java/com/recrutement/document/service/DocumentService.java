package com.recrutement.document.service;

import com.recrutement.config.FileStorageService;
import com.recrutement.document.model.CandidateDocument;
import com.recrutement.document.repository.CandidateDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final CandidateDocumentRepository repository;
    private final FileStorageService fileStorageService;

    public List<CandidateDocument> listByCandidate(Long candidateId) {
        return repository.findByCandidateId(candidateId);
    }

    public List<CandidateDocument> listByApplication(Long applicationId) {
        return repository.findByApplicationId(applicationId);
    }

    public CandidateDocument upload(Long candidateId, Long applicationId, String type, MultipartFile file) throws Exception {
        String url = fileStorageService.store(file, "documents");
        CandidateDocument doc = CandidateDocument.builder()
                .candidateId(candidateId)
                .applicationId(applicationId)
                .type(type != null ? type : "AUTRE")
                .fileName(file.getOriginalFilename())
                .fileUrl(url)
                .uploadedAt(LocalDateTime.now())
                .build();
        return repository.save(doc);
    }
}
