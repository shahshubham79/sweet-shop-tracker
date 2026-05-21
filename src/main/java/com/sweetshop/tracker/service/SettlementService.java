package com.sweetshop.tracker.service;

import com.sweetshop.tracker.dto.DashboardStats;
import com.sweetshop.tracker.dto.SettlementSummary;
import com.sweetshop.tracker.model.AdvanceRecord;
import com.sweetshop.tracker.model.Employee;
import com.sweetshop.tracker.model.Settlement;
import com.sweetshop.tracker.model.WorkRecord;
import com.sweetshop.tracker.repository.AdvanceRecordRepository;
import com.sweetshop.tracker.repository.EmployeeRepository;
import com.sweetshop.tracker.repository.SettlementRepository;
import com.sweetshop.tracker.repository.WorkRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SettlementService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkRecordRepository workRecordRepository;

    @Autowired
    private AdvanceRecordRepository advanceRecordRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    public SettlementSummary calculateSummary(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        List<WorkRecord> workRecords = workRecordRepository.findByEmployeeIdAndSettled(employeeId, false);
        List<AdvanceRecord> advanceRecords = advanceRecordRepository.findByEmployeeIdAndSettled(employeeId, false);

        int totalDays = workRecords.stream().mapToInt(WorkRecord::getDaysWorked).sum();
        
        BigDecimal totalEarned = employee.getDailyWageRate().multiply(BigDecimal.valueOf(totalDays));
        
        BigDecimal totalAdvances = advanceRecords.stream()
                .map(AdvanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netPayable = totalEarned.subtract(totalAdvances);

        return SettlementSummary.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .dailyWageRate(employee.getDailyWageRate())
                .activeWorkRecords(workRecords)
                .activeAdvanceRecords(advanceRecords)
                .totalDaysWorked(totalDays)
                .totalEarned(totalEarned)
                .totalAdvances(totalAdvances)
                .netPayable(netPayable)
                .build();
    }

    @Transactional
    public Settlement recordSettlement(Long employeeId, String remarks) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        SettlementSummary summary = calculateSummary(employeeId);
        
        if (summary.getActiveWorkRecords().isEmpty() && summary.getActiveAdvanceRecords().isEmpty()) {
            throw new IllegalStateException("No outstanding work records or advances to settle.");
        }

        LocalDate startDate = summary.getActiveWorkRecords().stream()
                .map(WorkRecord::getStartDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate endDate = summary.getActiveWorkRecords().stream()
                .map(WorkRecord::getEndDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        Settlement settlement = Settlement.builder()
                .employee(employee)
                .settlementDate(LocalDate.now())
                .startDate(summary.getActiveWorkRecords().isEmpty() ? null : startDate)
                .endDate(summary.getActiveWorkRecords().isEmpty() ? null : endDate)
                .totalDaysWorked(summary.getTotalDaysWorked())
                .totalEarned(summary.getTotalEarned())
                .totalAdvanceSubtracted(summary.getTotalAdvances())
                .netPaid(summary.getNetPayable())
                .remarks(remarks)
                .build();

        // Save the settlement first so it has an ID
        Settlement savedSettlement = settlementRepository.save(settlement);

        // Update work records
        for (WorkRecord wr : summary.getActiveWorkRecords()) {
            wr.setSettled(true);
            wr.setSettlement(savedSettlement);
            workRecordRepository.save(wr);
        }

        // Update advance records
        for (AdvanceRecord ar : summary.getActiveAdvanceRecords()) {
            ar.setSettled(true);
            ar.setSettlement(savedSettlement);
            advanceRecordRepository.save(ar);
        }

        return savedSettlement;
    }

    public DashboardStats getDashboardStats() {
        long totalActive = employeeRepository.findByActive(true).size();

        List<AdvanceRecord> activeAdvances = advanceRecordRepository.findBySettledFalseOrderByDateAsc();
        BigDecimal totalOutstandingAdvances = activeAdvances.stream()
                .map(AdvanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<WorkRecord> activeWorkRecords = workRecordRepository.findBySettledFalseOrderByStartDateAsc();
        int totalUnpaidDays = activeWorkRecords.stream().mapToInt(WorkRecord::getDaysWorked).sum();

        BigDecimal totalUnpaidEarned = BigDecimal.ZERO;
        for (WorkRecord wr : activeWorkRecords) {
            BigDecimal rate = wr.getEmployee().getDailyWageRate();
            BigDecimal earned = rate.multiply(BigDecimal.valueOf(wr.getDaysWorked()));
            totalUnpaidEarned = totalUnpaidEarned.add(earned);
        }

        List<Settlement> recentSettlements = settlementRepository.findAllByOrderBySettlementDateDesc();
        if (recentSettlements.size() > 5) {
            recentSettlements = recentSettlements.subList(0, 5);
        }

        return DashboardStats.builder()
                .totalActiveEmployees(totalActive)
                .totalOutstandingAdvances(totalOutstandingAdvances)
                .totalUnpaidDaysWorked(totalUnpaidDays)
                .totalUnpaidEarned(totalUnpaidEarned)
                .recentSettlements(recentSettlements)
                .build();
    }
}
