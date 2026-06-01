package com.recrutement.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardKpiDto {
    private long totalApplications;
    private long totalCandidates;
    private long openJobs;
    private Map<String, Long> applicationsByStep;
    private Map<String, Long> applicationsByStatus;
    private Map<String, Long> applicationsByJob;
    private double averageDaysInPipeline;
    private List<StaleApplicationDto> staleApplications;
}
