package com.sfsto.repository;

import com.sfsto.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    @Query("select s from Station s join s.services ss where ss.service.id = :serviceId")
    List<Station> findByServiceId(@Param("serviceId") Long serviceId);
}
