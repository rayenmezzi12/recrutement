package com.recrutement.dashboard.service;

import com.recrutement.candidate.repository.CandidateRepository;
import com.recrutement.dashboard.dto.DashboardKpiDto;
import com.recrutement.dashboard.dto.StaleApplicationDto;
import com.recrutement.recruitment.model.Application;
import com.recrutement.recruitment.model.ApplicationStatus;
import com.recrutement.recruitment.repository.ApplicationRepository;
import com.recrutement.recruitment.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    public DashboardKpiDto getKpis(int staleDays, String department) {
        List<Application> apps = applicationRepository.findAll().stream()
                .filter(a -> !Boolean.TRUE.equals(a.getArchived()))
                .toList();

        if (department != null && !department.isBlank()) {
            Set<Long> jobIds = jobRepository.findAll().stream()
                    .filter(j -> department.equalsIgnoreCase(j.getDepartment()))
                    .map(j -> j.getId())
                    .collect(Collectors.toSet());
            apps = apps.stream().filter(a -> jobIds.contains(a.getJobId())).toList();
        }

        Map<String, Long> byStep = apps.stream()
                .collect(Collectors.groupingBy(a -> a.getCurrentStep().name(), Collectors.counting()));
        Map<String, Long> byStatus = apps.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));
        Map<String, Long> byJob = apps.stream()
                .collect(Collectors.groupingBy(a -> String.valueOf(a.getJobId()), Collectors.counting()));

        double avgDays = apps.stream()
                .filter(a -> a.getAppliedDate() != null)
                .mapToLong(a -> ChronoUnit.DAYS.between(a.getAppliedDate(), LocalDate.now()))
                .average()
                .orElse(0);

        LocalDate threshold = LocalDate.now().minusDays(staleDays);
        List<StaleApplicationDto> stale = apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.EN_ATTENTE)
                .filter(a -> a.getAppliedDate() != null && a.getAppliedDate().isBefore(threshold))
                .map(a -> StaleApplicationDto.builder()
                        .applicationId(a.getId())
                        .candidateId(a.getCandidateId())
                        .jobId(a.getJobId())
                        .currentStep(a.getCurrentStep().name())
                        .daysWaiting(ChronoUnit.DAYS.between(a.getAppliedDate(), LocalDate.now()))
                        .build())
                .toList();

        return DashboardKpiDto.builder()
                .totalApplications(apps.size())
                .totalCandidates(candidateRepository.count())
                .openJobs(jobRepository.findAll().stream().filter(j -> "OPEN".equals(j.getStatus())).count())
                .applicationsByStep(byStep)
                .applicationsByStatus(byStatus)
                .applicationsByJob(byJob)
                .averageDaysInPipeline(avgDays)
                .staleApplications(stale)
                .build();
    }
}
