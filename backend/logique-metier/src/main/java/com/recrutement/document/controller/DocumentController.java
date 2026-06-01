package com.recrutement.document.controller;

import com.recrutement.document.model.CandidateDocument;
import com.recrutement.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CandidateDocument>> byCandidate(@PathVariable("candidateId") Long candidateId) {
        return ResponseEntity.ok(documentService.listByCandidate(candidateId));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<CandidateDocument>> byApplication(@PathVariable("applicationId") Long applicationId) {
        return ResponseEntity.ok(documentService.listByApplication(applicationId));
    }

    @PostMapping("/upload")
    public ResponseEntity<CandidateDocument> upload(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam(value = "applicationId", required = false) Long applicationId,
            @RequestParam(value = "type", defaultValue = "CV") String type,
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(documentService.upload(candidateId, applicationId, type, file));
    }
}
