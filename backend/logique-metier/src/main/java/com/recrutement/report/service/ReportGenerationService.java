package com.recrutement.report.service;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.repository.CandidateRepository;
import com.recrutement.report.dto.ReportDto;
import com.recrutement.report.dto.ReportRequestDto;
import com.recrutement.recruitment.model.Application;
import com.recrutement.recruitment.model.Job;
import com.recrutement.recruitment.repository.ApplicationRepository;
import com.recrutement.recruitment.repository.JobRepository;
import com.opencsv.CSVWriter;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportGenerationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    public ReportDto generateReport(ReportRequestDto request) {
        LocalDate from = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusMonths(1);
        LocalDate to = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();

        List<Application> apps = applicationRepository.findAll().stream()
                .filter(a -> a.getAppliedDate() != null)
                .filter(a -> !a.getAppliedDate().isBefore(from) && !a.getAppliedDate().isAfter(to))
                .toList();

        Map<Long, Candidate> candidates = candidateRepository.findAll().stream()
                .collect(Collectors.toMap(Candidate::getId, c -> c, (a, b) -> a));
        Map<Long, Job> jobs = jobRepository.findAll().stream()
                .collect(Collectors.toMap(Job::getId, j -> j, (a, b) -> a));

        ReportDto dto = new ReportDto();
        dto.setTitle("Rapport de recrutement");
        dto.setDescription("Période du " + from + " au " + to + " — " + apps.size() + " candidature(s)");
        dto.setGeneratedAt(LocalDateTime.now());

        if ("PDF".equalsIgnoreCase(request.getType())) {
            dto.setData(createPdfReport(apps, candidates, jobs, from, to));
        } else if ("CSV".equalsIgnoreCase(request.getType())) {
            dto.setData(createCsvReport(apps, candidates, jobs));
        } else {
            throw new IllegalArgumentException("Type de rapport non supporté : " + request.getType());
        }
        return dto;
    }

    private byte[] createPdfReport(List<Application> apps, Map<Long, Candidate> candidates,
                                   Map<Long, Job> jobs, LocalDate from, LocalDate to) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Rapport de recrutement — PFA"));
            document.add(new Paragraph("Période : " + from + " → " + to));
            document.add(new Paragraph("Généré le : " + LocalDateTime.now()));
            document.add(new Paragraph("Total candidatures : " + apps.size()));
            document.add(new Paragraph("\nDétail :"));
            for (Application app : apps) {
                Candidate c = candidates.get(app.getCandidateId());
                Job j = jobs.get(app.getJobId());
                String name = c != null ? c.getFirstName() + " " + c.getLastName() : "Candidat #" + app.getCandidateId();
                String poste = j != null ? j.getTitle() : "Poste #" + app.getJobId();
                String line = String.format("- %s | %s | étape %s | statut %s | note %.1f",
                        name, poste, app.getCurrentStep(), app.getStatus(),
                        app.getGlobalScore() != null ? app.getGlobalScore() : 0.0);
                document.add(new Paragraph(line));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            throw new RuntimeException(e);
        }
    }

    private byte[] createCsvReport(List<Application> apps, Map<Long, Candidate> candidates, Map<Long, Job> jobs) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos);
             CSVWriter writer = new CSVWriter(osw)) {
            writer.writeNext(new String[]{"Candidat", "Email", "Poste", "Département", "Étape", "Statut", "Note globale", "Date soumission"});
            List<String[]> rows = new ArrayList<>();
            for (Application app : apps) {
                Candidate c = candidates.get(app.getCandidateId());
                Job j = jobs.get(app.getJobId());
                rows.add(new String[]{
                        c != null ? c.getFirstName() + " " + c.getLastName() : "",
                        c != null ? c.getEmail() : "",
                        j != null ? j.getTitle() : "",
                        j != null ? j.getDepartment() : "",
                        app.getCurrentStep() != null ? app.getCurrentStep().name() : "",
                        app.getStatus() != null ? app.getStatus().name() : "",
                        app.getGlobalScore() != null ? String.valueOf(app.getGlobalScore()) : "",
                        app.getAppliedDate() != null ? app.getAppliedDate().toString() : ""
                });
            }
            writer.writeAll(rows);
            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating CSV report", e);
            throw new RuntimeException(e);
        }
    }
}
