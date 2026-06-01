package com.recrutement.history.repository;

import com.recrutement.history.model.ActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionHistoryRepository extends JpaRepository<ActionHistory, Long> {
    List<ActionHistory> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    List<ActionHistory> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);

    @Query("SELECT h FROM ActionHistory h WHERE h.actionType = :actionType ORDER BY h.createdAt DESC")
    List<ActionHistory> findByActionTypeOrderByCreatedAtDesc(@Param("actionType") String actionType);
}
