package com.recrutement.recruitment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

    private Long candidateId;

    @Enumerated(EnumType.STRING)
    private RecruitmentStep currentStep;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate appliedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String recruiterUsername;

    @Builder.Default
    private Boolean archived = false;

    /** Note globale (moyenne des évaluations liées — calculerNoteGlobale) */
    private Double globalScore;
}
