package com.recrutement.interview.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Long applicationId;

    private String interviewerName;

    private LocalDateTime interviewDate;

    private String type; // ONLINE, IN_PERSON

    private String location;

    private String status; // SCHEDULED, COMPLETED, CANCELLED

    @Column(length = 1000)
    private String feedback;
}
