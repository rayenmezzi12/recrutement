package com.recrutement.interview.controller;

import com.recrutement.interview.model.Evaluation;
import com.recrutement.interview.model.Interview;
import com.recrutement.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping
    public ResponseEntity<List<Interview>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Interview> getInterviewById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Interview>> getInterviewsByCandidate(
            @PathVariable("candidateId") Long candidateId) {
        return ResponseEntity.ok(interviewService.getInterviewsByCandidate(candidateId));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Interview>> getInterviewsByApplication(
            @PathVariable("applicationId") Long applicationId) {
        return ResponseEntity.ok(interviewService.getInterviewsByApplication(applicationId));
    }

    @PostMapping
    public ResponseEntity<Interview> scheduleInterview(@RequestBody Interview interview) {
        return ResponseEntity.ok(interviewService.scheduleInterview(interview));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Interview> updateStatus(
            @PathVariable("id") Long id, @RequestParam("status") String status) {
        return ResponseEntity.ok(interviewService.updateInterviewStatus(id, status));
    }

    @PostMapping("/evaluations")
    public ResponseEntity<Evaluation> submitEvaluation(@RequestBody Evaluation evaluation) {
        return ResponseEntity.ok(interviewService.submitEvaluation(evaluation));
    }

    @GetMapping("/{interviewId}/evaluation")
    public ResponseEntity<Evaluation> getEvaluation(@PathVariable("interviewId") Long interviewId) {
        return ResponseEntity.ok(interviewService.getEvaluationByInterview(interviewId));
    }
}
