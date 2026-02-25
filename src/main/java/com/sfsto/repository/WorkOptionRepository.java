package com.sfsto.repository;

import com.sfsto.model.WorkOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOptionRepository extends JpaRepository<WorkOption, Long> {
}
