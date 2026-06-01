package com.recrutement.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaleApplicationDto {
    private Long applicationId;
    private Long candidateId;
    private Long jobId;
    private String currentStep;
    private long daysWaiting;
}
