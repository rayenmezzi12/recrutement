package com.recrutement.report.dto;

import java.time.LocalDate;

public class ReportRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private String type; // PDF or CSV

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
