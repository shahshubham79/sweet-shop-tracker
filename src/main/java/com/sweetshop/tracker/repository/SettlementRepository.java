package com.sweetshop.tracker.repository;

import com.sweetshop.tracker.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByEmployeeIdOrderBySettlementDateDesc(Long employeeId);
    List<Settlement> findAllByOrderBySettlementDateDesc();
}
