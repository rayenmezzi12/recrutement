package com.recrutement.history.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;

    private Long applicationId;

    @Column(nullable = false)
    private String actionType;

    private String actorUsername;

    @Column(length = 2000)
    private String details;

    private LocalDateTime createdAt;
}
