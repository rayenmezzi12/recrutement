package com.recrutement.document.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;

    private Long applicationId;

    @Column(nullable = false)
    private String type; // CV, LETTRE_MOTIVATION, AUTRE

    private String fileName;

    @Column(length = 500)
    private String fileUrl;

    private LocalDateTime uploadedAt;
}
