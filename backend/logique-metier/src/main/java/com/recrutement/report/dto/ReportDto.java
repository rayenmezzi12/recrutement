package com.recrutement.report.dto;

import java.time.LocalDateTime;

public class ReportDto {
    private String title;
    private String description;
    private LocalDateTime generatedAt;
    private byte[] data; // PDF or CSV bytes

    // getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
