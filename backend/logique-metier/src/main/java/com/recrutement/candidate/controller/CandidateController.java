package com.recrutement.candidate.controller;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.service.CandidateService;
import com.recrutement.config.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN')or hasRole('RESPONSABLE_DEPT')")
    public ResponseEntity<List<Candidate>> getAllCandidates(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "search", required = false) String search) {
        if (title != null || skills != null || search != null) {
            return ResponseEntity.ok(candidateService.searchCandidates(title, skills, search));
        }
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN') or @candidateSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN') or #username == authentication.name")
    public ResponseEntity<Candidate> getCandidateByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(candidateService.getCandidateByUsername(username));
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDAT') or hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN')")
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate candidate) {
        return ResponseEntity.ok(candidateService.createCandidate(candidate));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN') or @candidateSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<Candidate> updateCandidate(
            @PathVariable("id") Long id, @RequestBody Candidate candidate) {
        return ResponseEntity.ok(candidateService.updateCandidate(id, candidate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCandidate(@PathVariable("id") Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload-cv")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN') or @candidateSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<Candidate> uploadCv(
            @PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws Exception {
        String url = fileStorageService.store(file, "cv");
        Candidate candidate = candidateService.getCandidateById(id);
        candidate.setResumeUrl(url);
        return ResponseEntity.ok(candidateService.updateCandidate(id, candidate));
    }

    @PostMapping("/{id}/upload-cover-letter")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN') or @candidateSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<Map<String, String>> uploadCoverLetter(
            @PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws Exception {
        String url = fileStorageService.store(file, "cover-letters");
        Candidate candidate = candidateService.getCandidateById(id);
        candidate.setCoverLetter("Document joint : " + url);
        candidateService.updateCandidate(id, candidate);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
