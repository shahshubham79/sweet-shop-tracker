package com.sweetshop.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "advance_records")
public class AdvanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal amount;

    private String remarks;

    @Column(nullable = false)
    private Boolean settled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    @JsonIgnoreProperties({"workRecords", "advanceRecords"})
    private Settlement settlement;

    // Constructors
    public AdvanceRecord() {}

    public AdvanceRecord(Long id, Employee employee, LocalDate date, BigDecimal amount, String remarks,
                         Boolean settled, Settlement settlement) {
        this.id = id;
        this.employee = employee;
        this.date = date;
        this.amount = amount;
        this.remarks = remarks;
        this.settled = settled != null ? settled : false;
        this.settlement = settlement;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Boolean getSettled() { return settled; }
    public void setSettled(Boolean settled) { this.settled = settled; }

    public Settlement getSettlement() { return settlement; }
    public void setSettlement(Settlement settlement) { this.settlement = settlement; }

    // Builder Pattern
    public static AdvanceRecordBuilder builder() {
        return new AdvanceRecordBuilder();
    }

    public static class AdvanceRecordBuilder {
        private Long id;
        private Employee employee;
        private LocalDate date;
        private BigDecimal amount;
        private String remarks;
        private Boolean settled = false;
        private Settlement settlement;

        public AdvanceRecordBuilder id(Long id) { this.id = id; return this; }
        public AdvanceRecordBuilder employee(Employee employee) { this.employee = employee; return this; }
        public AdvanceRecordBuilder date(LocalDate date) { this.date = date; return this; }
        public AdvanceRecordBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public AdvanceRecordBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public AdvanceRecordBuilder settled(Boolean settled) { this.settled = settled; return this; }
        public AdvanceRecordBuilder settlement(Settlement settlement) { this.settlement = settlement; return this; }

        public AdvanceRecord build() {
            return new AdvanceRecord(id, employee, date, amount, remarks, settled, settlement);
        }
    }
}
