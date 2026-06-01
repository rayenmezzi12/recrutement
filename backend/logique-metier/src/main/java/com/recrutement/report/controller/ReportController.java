package com.recrutement.report.controller;

import com.recrutement.report.dto.ReportDto;
import com.recrutement.report.dto.ReportRequestDto;
import com.recrutement.report.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportGenerationService reportGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@RequestBody ReportRequestDto request) {
        ReportDto report = reportGenerationService.generateReport(request);
        String type = request.getType() != null ? request.getType().toUpperCase() : "PDF";
        MediaType mediaType = "CSV".equals(type)
                ? MediaType.parseMediaType("text/csv")
                : MediaType.APPLICATION_PDF;
        String filename = "CSV".equals(type) ? "rapport.csv" : "rapport.pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(report.getData());
    }
}
