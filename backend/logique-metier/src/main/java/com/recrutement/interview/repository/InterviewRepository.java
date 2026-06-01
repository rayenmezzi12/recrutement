package com.recrutement.interview.repository;

import com.recrutement.interview.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByCandidateId(Long candidateId);
    List<Interview> findByApplicationId(Long applicationId);
}
