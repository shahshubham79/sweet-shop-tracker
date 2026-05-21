package com.sweetshop.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_days_worked", nullable = false)
    private Integer totalDaysWorked;

    @Column(name = "total_earned", nullable = false)
    private BigDecimal totalEarned;

    @Column(name = "total_advance_subtracted", nullable = false)
    private BigDecimal totalAdvanceSubtracted;

    @Column(name = "net_paid", nullable = false)
    private BigDecimal netPaid;

    private String remarks;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("settlement")
    private List<WorkRecord> workRecords;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("settlement")
    private List<AdvanceRecord> advanceRecords;

    // Constructors
    public Settlement() {}

    public Settlement(Long id, Employee employee, LocalDate settlementDate, LocalDate startDate, LocalDate endDate,
                      Integer totalDaysWorked, BigDecimal totalEarned, BigDecimal totalAdvanceSubtracted,
                      BigDecimal netPaid, String remarks, List<WorkRecord> workRecords, List<AdvanceRecord> advanceRecords) {
        this.id = id;
        this.employee = employee;
        this.settlementDate = settlementDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDaysWorked = totalDaysWorked;
        this.totalEarned = totalEarned;
        this.totalAdvanceSubtracted = totalAdvanceSubtracted;
        this.netPaid = netPaid;
        this.remarks = remarks;
        this.workRecords = workRecords;
        this.advanceRecords = advanceRecords;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDate settlementDate) { this.settlementDate = settlementDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTotalDaysWorked() { return totalDaysWorked; }
    public void setTotalDaysWorked(Integer totalDaysWorked) { this.totalDaysWorked = totalDaysWorked; }

    public BigDecimal getTotalEarned() { return totalEarned; }
    public void setTotalEarned(BigDecimal totalEarned) { this.totalEarned = totalEarned; }

    public BigDecimal getTotalAdvanceSubtracted() { return totalAdvanceSubtracted; }
    public void setTotalAdvanceSubtracted(BigDecimal totalAdvanceSubtracted) { this.totalAdvanceSubtracted = totalAdvanceSubtracted; }

    public BigDecimal getNetPaid() { return netPaid; }
    public void setNetPaid(BigDecimal netPaid) { this.netPaid = netPaid; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public List<WorkRecord> getWorkRecords() { return workRecords; }
    public void setWorkRecords(List<WorkRecord> workRecords) { this.workRecords = workRecords; }

    public List<AdvanceRecord> getAdvanceRecords() { return advanceRecords; }
    public void setAdvanceRecords(List<AdvanceRecord> advanceRecords) { this.advanceRecords = advanceRecords; }

    // Builder Pattern
    public static SettlementBuilder builder() {
        return new SettlementBuilder();
    }

    public static class SettlementBuilder {
        private Long id;
        private Employee employee;
        private LocalDate settlementDate;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer totalDaysWorked;
        private BigDecimal totalEarned;
        private BigDecimal totalAdvanceSubtracted;
        private BigDecimal netPaid;
        private String remarks;
        private List<WorkRecord> workRecords;
        private List<AdvanceRecord> advanceRecords;

        public SettlementBuilder id(Long id) { this.id = id; return this; }
        public SettlementBuilder employee(Employee employee) { this.employee = employee; return this; }
        public SettlementBuilder settlementDate(LocalDate settlementDate) { this.settlementDate = settlementDate; return this; }
        public SettlementBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public SettlementBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public SettlementBuilder totalDaysWorked(Integer totalDaysWorked) { this.totalDaysWorked = totalDaysWorked; return this; }
        public SettlementBuilder totalEarned(BigDecimal totalEarned) { this.totalEarned = totalEarned; return this; }
        public SettlementBuilder totalAdvanceSubtracted(BigDecimal totalAdvanceSubtracted) { this.totalAdvanceSubtracted = totalAdvanceSubtracted; return this; }
        public SettlementBuilder netPaid(BigDecimal netPaid) { this.netPaid = netPaid; return this; }
        public SettlementBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public SettlementBuilder workRecords(List<WorkRecord> workRecords) { this.workRecords = workRecords; return this; }
        public SettlementBuilder advanceRecords(List<AdvanceRecord> advanceRecords) { this.advanceRecords = advanceRecords; return this; }

        public Settlement build() {
            return new Settlement(id, employee, settlementDate, startDate, endDate, totalDaysWorked, totalEarned, totalAdvanceSubtracted, netPaid, remarks, workRecords, advanceRecords);
        }
    }
}
