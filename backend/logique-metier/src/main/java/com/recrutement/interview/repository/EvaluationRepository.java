package com.recrutement.interview.repository;

import com.recrutement.interview.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByInterviewId(Long interviewId);

    @Query("""
            SELECT e FROM Evaluation e, Interview i
            WHERE e.interviewId = i.id AND i.applicationId = :applicationId
            """)
    List<Evaluation> findAllByApplicationId(@Param("applicationId") Long applicationId);
}
