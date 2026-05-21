package com.sweetshop.tracker.dto;

import com.sweetshop.tracker.model.AdvanceRecord;
import com.sweetshop.tracker.model.WorkRecord;
import java.math.BigDecimal;
import java.util.List;

public class SettlementSummary {
    private Long employeeId;
    private String employeeName;
    private BigDecimal dailyWageRate;
    private List<WorkRecord> activeWorkRecords;
    private List<AdvanceRecord> activeAdvanceRecords;
    
    private Integer totalDaysWorked;
    private BigDecimal totalEarned;
    private BigDecimal totalAdvances;
    private BigDecimal netPayable;

    // Constructors
    public SettlementSummary() {}

    public SettlementSummary(Long employeeId, String employeeName, BigDecimal dailyWageRate,
                             List<WorkRecord> activeWorkRecords, List<AdvanceRecord> activeAdvanceRecords,
                             Integer totalDaysWorked, BigDecimal totalEarned, BigDecimal totalAdvances, BigDecimal netPayable) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.dailyWageRate = dailyWageRate;
        this.activeWorkRecords = activeWorkRecords;
        this.activeAdvanceRecords = activeAdvanceRecords;
        this.totalDaysWorked = totalDaysWorked;
        this.totalEarned = totalEarned;
        this.totalAdvances = totalAdvances;
        this.netPayable = netPayable;
    }

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public BigDecimal getDailyWageRate() { return dailyWageRate; }
    public void setDailyWageRate(BigDecimal dailyWageRate) { this.dailyWageRate = dailyWageRate; }

    public List<WorkRecord> getActiveWorkRecords() { return activeWorkRecords; }
    public void setActiveWorkRecords(List<WorkRecord> activeWorkRecords) { this.activeWorkRecords = activeWorkRecords; }

    public List<AdvanceRecord> getActiveAdvanceRecords() { return activeAdvanceRecords; }
    public void setActiveAdvanceRecords(List<AdvanceRecord> activeAdvanceRecords) { this.activeAdvanceRecords = activeAdvanceRecords; }

    public Integer getTotalDaysWorked() { return totalDaysWorked; }
    public void setTotalDaysWorked(Integer totalDaysWorked) { this.totalDaysWorked = totalDaysWorked; }

    public BigDecimal getTotalEarned() { return totalEarned; }
    public void setTotalEarned(BigDecimal totalEarned) { this.totalEarned = totalEarned; }

    public BigDecimal getTotalAdvances() { return totalAdvances; }
    public void setTotalAdvances(BigDecimal totalAdvances) { this.totalAdvances = totalAdvances; }

    public BigDecimal getNetPayable() { return netPayable; }
    public void setNetPayable(BigDecimal netPayable) { this.netPayable = netPayable; }

    // Builder Pattern
    public static SettlementSummaryBuilder builder() {
        return new SettlementSummaryBuilder();
    }

    public static class SettlementSummaryBuilder {
        private Long employeeId;
        private String employeeName;
        private BigDecimal dailyWageRate;
        private List<WorkRecord> activeWorkRecords;
        private List<AdvanceRecord> activeAdvanceRecords;
        private Integer totalDaysWorked;
        private BigDecimal totalEarned;
        private BigDecimal totalAdvances;
        private BigDecimal netPayable;

        public SettlementSummaryBuilder employeeId(Long employeeId) { this.employeeId = employeeId; return this; }
        public SettlementSummaryBuilder employeeName(String employeeName) { this.employeeName = employeeName; return this; }
        public SettlementSummaryBuilder dailyWageRate(BigDecimal dailyWageRate) { this.dailyWageRate = dailyWageRate; return this; }
        public SettlementSummaryBuilder activeWorkRecords(List<WorkRecord> activeWorkRecords) { this.activeWorkRecords = activeWorkRecords; return this; }
        public SettlementSummaryBuilder activeAdvanceRecords(List<AdvanceRecord> activeAdvanceRecords) { this.activeAdvanceRecords = activeAdvanceRecords; return this; }
        public SettlementSummaryBuilder totalDaysWorked(Integer totalDaysWorked) { this.totalDaysWorked = totalDaysWorked; return this; }
        public SettlementSummaryBuilder totalEarned(BigDecimal totalEarned) { this.totalEarned = totalEarned; return this; }
        public SettlementSummaryBuilder totalAdvances(BigDecimal totalAdvances) { this.totalAdvances = totalAdvances; return this; }
        public SettlementSummaryBuilder netPayable(BigDecimal netPayable) { this.netPayable = netPayable; return this; }

        public SettlementSummary build() {
            return new SettlementSummary(employeeId, employeeName, dailyWageRate, activeWorkRecords, activeAdvanceRecords, totalDaysWorked, totalEarned, totalAdvances, netPayable);
        }
    }
}
