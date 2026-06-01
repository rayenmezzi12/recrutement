package com.recrutement.candidate.service;

import com.recrutement.candidate.model.Candidate;
import com.recrutement.candidate.repository.CandidateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    @PostConstruct
    public void initCandidates() {
        if (candidateRepository.count() == 0) {
            Candidate c1 = Candidate.builder()
                    .firstName("Alice")
                    .lastName("Smith")
                    .email("alice.smith@email.com")
                    .phone("+33612345678")
                    .username("candidat") // link to seeded user in auth-service
                    .title("Développeuse Java Spring Boot")
                    .skills("Java, Spring Boot, PostgreSQL, Docker")
                    .experienceYears(3)
                    .resumeUrl("/uploads/cv-alice.pdf")
                    .coverLetter("Passionnée par l'écosystème Spring...")
                    .build();
            candidateRepository.save(c1);

            Candidate c2 = Candidate.builder()
                    .firstName("Bob")
                    .lastName("Martin")
                    .email("bob.martin@email.com")
                    .phone("+33698765432")
                    .title("Développeur Front Angular")
                    .skills("Angular, TypeScript, CSS, TailwindCSS")
                    .experienceYears(5)
                    .resumeUrl("/uploads/cv-bob.pdf")
                    .coverLetter("Expert Angular à la recherche de nouveaux challenges...")
                    .build();
            candidateRepository.save(c2);
        }
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Candidate> searchCandidates(String title, String skills, String search) {
        return candidateRepository.search(
                blankToEmpty(title),
                blankToEmpty(skills),
                blankToEmpty(search));
    }

    private String blankToEmpty(String value) {
        return value == null || value.isBlank() ? "" : value;
    }

    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));
    }

    public Candidate getCandidateByUsername(String username) {
        return candidateRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé pour l'utilisateur: " + username));
    }

    public Candidate createCandidate(Candidate candidate) {
        if (candidate.getEmail() == null || candidate.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (candidate.getFirstName() == null || candidate.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (candidate.getLastName() == null || candidate.getLastName().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        candidate.setEmail(candidate.getEmail().trim());
        if (candidateRepository.findByEmail(candidate.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un candidat existe déjà avec cet email");
        }
        if (candidate.getExperienceYears() == null) {
            candidate.setExperienceYears(0);
        }
        if ((candidate.getUsername() == null || candidate.getUsername().isBlank())
                && candidate.getEmail().contains("@")) {
            candidate.setUsername(candidate.getEmail().substring(0, candidate.getEmail().indexOf('@')).trim());
        }
        return candidateRepository.save(candidate);
    }

    public Candidate updateCandidate(Long id, Candidate details) {
        Candidate candidate = getCandidateById(id);
        candidate.setFirstName(details.getFirstName());
        candidate.setLastName(details.getLastName());
        candidate.setEmail(details.getEmail());
        candidate.setPhone(details.getPhone());
        candidate.setTitle(details.getTitle());
        candidate.setSkills(details.getSkills());
        candidate.setExperienceYears(details.getExperienceYears());
        candidate.setResumeUrl(details.getResumeUrl());
        candidate.setCoverLetter(details.getCoverLetter());
        candidate.setAddress(details.getAddress());
        candidate.setDateOfBirth(details.getDateOfBirth());
        candidate.setLinkedin(details.getLinkedin());
        return candidateRepository.save(candidate);
    }

    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }
}
