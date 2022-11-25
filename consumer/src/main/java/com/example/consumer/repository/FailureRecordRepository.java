package com.example.consumer.repository;

import com.example.consumer.entity.FailureRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailureRecordRepository extends JpaRepository<FailureRecord, Integer> {
}
