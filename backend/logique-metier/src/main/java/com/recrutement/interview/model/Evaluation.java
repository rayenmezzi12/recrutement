package com.recrutement.interview.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long interviewId;

    private Integer technicalRating; // 1 to 5

    private Integer communicationRating; // 1 to 5

    @Column(length = 2000)
    private String generalComments;

    private String recommendation; // HIRE, NO_HIRE, HOLD

    private Double globalScore;

    /** Critères JSON : {"technique":4,"communication":5,...} */
    @Column(name = "criteria_json", length = 2000)
    private String criteriaJson;
}
