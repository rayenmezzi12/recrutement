package com.recrutement.recruitment.repository;

import com.recrutement.recruitment.model.Application;
import com.recrutement.recruitment.model.ApplicationStatus;
import com.recrutement.recruitment.model.RecruitmentStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidateId(Long candidateId);
    List<Application> findByJobId(Long jobId);

    @Query("""
            SELECT a FROM Application a WHERE
            (:jobId IS NULL OR a.jobId = :jobId) AND
            (:candidateId IS NULL OR a.candidateId = :candidateId) AND
            (:step IS NULL OR a.currentStep = :step) AND
            (:status IS NULL OR a.status = :status) AND
            (:recruiterUsername IS NULL OR a.recruiterUsername = :recruiterUsername) AND
            (:archived IS NULL OR a.archived = :archived) AND
            (:fromDate IS NULL OR a.appliedDate >= :fromDate) AND
            (:toDate IS NULL OR a.appliedDate <= :toDate)
            """)
    List<Application> search(
            @Param("jobId") Long jobId,
            @Param("candidateId") Long candidateId,
            @Param("step") RecruitmentStep step,
            @Param("status") ApplicationStatus status,
            @Param("recruiterUsername") String recruiterUsername,
            @Param("archived") Boolean archived,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    long countByJobIdAndCurrentStep(Long jobId, RecruitmentStep step);

    long countByCurrentStep(RecruitmentStep step);

    List<Application> findByStatusAndAppliedDateBefore(ApplicationStatus status, LocalDate date);
}
