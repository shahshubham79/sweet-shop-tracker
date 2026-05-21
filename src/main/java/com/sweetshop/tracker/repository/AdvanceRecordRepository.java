package com.sweetshop.tracker.repository;

import com.sweetshop.tracker.model.AdvanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvanceRecordRepository extends JpaRepository<AdvanceRecord, Long> {
    List<AdvanceRecord> findByEmployeeIdAndSettled(Long employeeId, Boolean settled);
    List<AdvanceRecord> findBySettlementId(Long settlementId);
    List<AdvanceRecord> findBySettledFalseOrderByDateAsc();
}
