package com.recrutement.offer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "offers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Long applicationId;

    private Double salaryOffer;

    private String positionTitle;

    @Column(length = 5000)
    private String offerLetterContent;

    private String status; // GENERATED, SENT, ACCEPTED, REJECTED

    private LocalDate sentDate;

    private LocalDate expirationDate;
}
