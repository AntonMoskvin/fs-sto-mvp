package com.sfsto.repository;

import com.sfsto.model.StationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationServiceRepository extends JpaRepository<StationService, Long> {
}
