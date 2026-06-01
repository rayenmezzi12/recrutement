package com.recrutement.dashboard.controller;

import com.recrutement.dashboard.dto.DashboardKpiDto;
import com.recrutement.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKpiDto> kpis(
            @RequestParam(value = "staleDays", defaultValue = "7") int staleDays,
            @RequestParam(value = "department", required = false) String department) {
        return ResponseEntity.ok(dashboardService.getKpis(staleDays, department));
    }
}
