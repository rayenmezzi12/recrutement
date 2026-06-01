package com.recrutement.history.controller;

import com.recrutement.history.model.ActionHistory;
import com.recrutement.history.service.ActionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ActionHistoryController {

    private final ActionHistoryService actionHistoryService;

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ActionHistory>> byCandidate(@PathVariable("candidateId") Long candidateId) {
        return ResponseEntity.ok(actionHistoryService.getByCandidate(candidateId));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ActionHistory>> byApplication(@PathVariable("applicationId") Long applicationId) {
        return ResponseEntity.ok(actionHistoryService.getByApplication(applicationId));
    }
}
