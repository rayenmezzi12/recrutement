package com.recrutement.candidate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "candidates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String username;

    private String title;

    @Column(length = 1000)
    private String skills;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "cover_letter", length = 2000)
    private String coverLetter;

    private String address;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    private String linkedin;
}
