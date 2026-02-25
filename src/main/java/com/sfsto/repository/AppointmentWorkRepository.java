package com.sfsto.repository;

import com.sfsto.model.AppointmentWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentWorkRepository extends JpaRepository<AppointmentWork, Long> {
}
