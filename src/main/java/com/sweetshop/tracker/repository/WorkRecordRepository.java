package com.sweetshop.tracker.repository;

import com.sweetshop.tracker.model.WorkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRecordRepository extends JpaRepository<WorkRecord, Long> {
    List<WorkRecord> findByEmployeeIdAndSettled(Long employeeId, Boolean settled);
    List<WorkRecord> findBySettlementId(Long settlementId);
    List<WorkRecord> findBySettledFalseOrderByStartDateAsc();
}
