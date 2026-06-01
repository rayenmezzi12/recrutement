package com.recrutement.deptreview.controller;

import com.recrutement.deptreview.service.DeptReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dept-reviews")
@RequiredArgsConstructor
public class DeptReviewController {

    private final DeptReviewService deptReviewService;

    @PostMapping
    @PreAuthorize("hasRole('RESPONSABLE_DEPT') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> submit(@RequestBody Map<String, Object> body,
                                                     @RequestHeader(value = "X-Actor", defaultValue = "dept") String actor) {
        Long applicationId = Long.valueOf(body.get("applicationId").toString());
        String decision = body.get("decision").toString();
        int techRating = body.get("techRating") != null ? Integer.parseInt(body.get("techRating").toString()) : 0;
        int commRating = body.get("commRating") != null ? Integer.parseInt(body.get("commRating").toString()) : 0;
        int fitRating = body.get("fitRating") != null ? Integer.parseInt(body.get("fitRating").toString()) : 0;
        String comment = body.get("comment") != null ? body.get("comment").toString() : "";
        return ResponseEntity.ok(deptReviewService.submitReview(
                applicationId, actor, decision, techRating, commRating, fitRating, comment));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('RESPONSABLE_RH') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> recent() {
        return ResponseEntity.ok(deptReviewService.getRecentForRecruiter());
    }
}
