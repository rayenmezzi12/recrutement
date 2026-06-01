package com.recrutement.recruitment.controller;

import com.recrutement.recruitment.model.Application;
import com.recrutement.recruitment.model.ApplicationStatus;
import com.recrutement.recruitment.model.Job;
import com.recrutement.recruitment.model.RecruitmentStep;
import com.recrutement.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(recruitmentService.getAllJobs());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(recruitmentService.getJobById(id));
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(recruitmentService.createJob(job));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable("id") Long id, @RequestBody Job job) {
        return ResponseEntity.ok(recruitmentService.updateJob(id, job));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) {
        recruitmentService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getApplications(
            @RequestParam(value = "jobId", required = false) Long jobId,
            @RequestParam(value = "candidateId", required = false) Long candidateId,
            @RequestParam(value = "step", required = false) RecruitmentStep step,
            @RequestParam(value = "status", required = false) ApplicationStatus status,
            @RequestParam(value = "recruiterUsername", required = false) String recruiterUsername,
            @RequestParam(value = "archived", required = false) Boolean archived,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        if (jobId == null && candidateId == null && step == null && status == null
                && recruiterUsername == null && archived == null && fromDate == null && toDate == null) {
            return ResponseEntity.ok(recruitmentService.getAllApplications());
        }
        return ResponseEntity.ok(recruitmentService.searchApplications(
                jobId, candidateId, step, status, recruiterUsername, archived, fromDate, toDate));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(recruitmentService.getApplicationById(id));
    }

    @GetMapping("/applications/candidate/{candidateId}")
    public ResponseEntity<List<Application>> getApplicationsByCandidate(
            @PathVariable("candidateId") Long candidateId) {
        return ResponseEntity.ok(recruitmentService.getApplicationsByCandidate(candidateId));
    }

    @GetMapping("/applications/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok(recruitmentService.getApplicationsByJob(jobId));
    }

    @PostMapping("/applications")
    public ResponseEntity<Application> apply(
            @RequestBody Application application,
            @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return ResponseEntity.ok(recruitmentService.apply(application, actor));
    }

    @PutMapping("/applications/{id}/step")
    public ResponseEntity<Application> updateStep(
            @PathVariable("id") Long id,
            @RequestParam("step") RecruitmentStep step,
            @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return ResponseEntity.ok(recruitmentService.updateStep(id, step, actor));
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") ApplicationStatus status,
            @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return ResponseEntity.ok(recruitmentService.updateStatus(id, status, actor));
    }

    @PostMapping("/applications/{id}/note-globale")
    public ResponseEntity<Application> calculerNoteGlobale(@PathVariable("id") Long id) {
        return ResponseEntity.ok(recruitmentService.calculerNoteGlobale(id));
    }
}
