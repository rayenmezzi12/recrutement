package com.recrutement.candidate.repository;

import com.recrutement.candidate.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByUsername(String username);

    @Query(value = """
            SELECT * FROM candidates c WHERE
            (:title = '' OR LOWER(COALESCE(c.title, '')) LIKE LOWER(CONCAT('%', CAST(:title as text), '%'))) AND
            (:skills = '' OR LOWER(COALESCE(c.skills, '')) LIKE LOWER(CONCAT('%', CAST(:skills as text), '%'))) AND
            (:search = '' OR LOWER(CONCAT(COALESCE(c.first_name, ''), ' ', COALESCE(c.last_name, ''), ' ', COALESCE(c.email, ''))) LIKE LOWER(CONCAT('%', CAST(:search as text), '%')))
            """, nativeQuery = true)
    List<Candidate> search(
            @Param("title") String title,
            @Param("skills") String skills,
            @Param("search") String search
    );
}
