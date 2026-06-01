package com.recrutement.offer.repository;

import com.recrutement.offer.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByCandidateId(Long candidateId);
    Optional<Offer> findByApplicationId(Long applicationId);
}
