package com.example.chaseC.repository;

import com.example.chaseC.entity.TrackRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRequestRepository extends JpaRepository<TrackRequest, Long> {
    Optional<TrackRequest> findByHblNo(String hblNo);
}
