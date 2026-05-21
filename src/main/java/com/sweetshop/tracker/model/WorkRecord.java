package com.sweetshop.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_records")
public class WorkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "days_worked", nullable = false)
    private Integer daysWorked;

    private String remarks;

    @Column(nullable = false)
    private Boolean settled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    @JsonIgnoreProperties({"workRecords", "advanceRecords"})
    private Settlement settlement;

    // Constructors
    public WorkRecord() {}

    public WorkRecord(Long id, Employee employee, LocalDate startDate, LocalDate endDate, Integer daysWorked,
                      String remarks, Boolean settled, Settlement settlement) {
        this.id = id;
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysWorked = daysWorked;
        this.remarks = remarks;
        this.settled = settled != null ? settled : false;
        this.settlement = settlement;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getDaysWorked() { return daysWorked; }
    public void setDaysWorked(Integer daysWorked) { this.daysWorked = daysWorked; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Boolean getSettled() { return settled; }
    public void setSettled(Boolean settled) { this.settled = settled; }

    public Settlement getSettlement() { return settlement; }
    public void setSettlement(Settlement settlement) { this.settlement = settlement; }

    // Builder Pattern
    public static WorkRecordBuilder builder() {
        return new WorkRecordBuilder();
    }

    public static class WorkRecordBuilder {
        private Long id;
        private Employee employee;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer daysWorked;
        private String remarks;
        private Boolean settled = false;
        private Settlement settlement;

        public WorkRecordBuilder id(Long id) { this.id = id; return this; }
        public WorkRecordBuilder employee(Employee employee) { this.employee = employee; return this; }
        public WorkRecordBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public WorkRecordBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public WorkRecordBuilder daysWorked(Integer daysWorked) { this.daysWorked = daysWorked; return this; }
        public WorkRecordBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public WorkRecordBuilder settled(Boolean settled) { this.settled = settled; return this; }
        public WorkRecordBuilder settlement(Settlement settlement) { this.settlement = settlement; return this; }

        public WorkRecord build() {
            return new WorkRecord(id, employee, startDate, endDate, daysWorked, remarks, settled, settlement);
        }
    }
}
