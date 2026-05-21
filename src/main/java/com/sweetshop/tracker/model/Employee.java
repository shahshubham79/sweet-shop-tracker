package com.sweetshop.tracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(name = "daily_wage_rate", nullable = false)
    private BigDecimal dailyWageRate;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    private Boolean active = true;

    // Constructors
    public Employee() {}

    public Employee(Long id, String name, String phone, BigDecimal dailyWageRate, LocalDate joinDate, Boolean active) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.dailyWageRate = dailyWageRate;
        this.joinDate = joinDate;
        this.active = active != null ? active : true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getDailyWageRate() { return dailyWageRate; }
    public void setDailyWageRate(BigDecimal dailyWageRate) { this.dailyWageRate = dailyWageRate; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    // Builder Pattern
    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public static class EmployeeBuilder {
        private Long id;
        private String name;
        private String phone;
        private BigDecimal dailyWageRate;
        private LocalDate joinDate;
        private Boolean active = true;

        public EmployeeBuilder id(Long id) { this.id = id; return this; }
        public EmployeeBuilder name(String name) { this.name = name; return this; }
        public EmployeeBuilder phone(String phone) { this.phone = phone; return this; }
        public EmployeeBuilder dailyWageRate(BigDecimal dailyWageRate) { this.dailyWageRate = dailyWageRate; return this; }
        public EmployeeBuilder joinDate(LocalDate joinDate) { this.joinDate = joinDate; return this; }
        public EmployeeBuilder active(Boolean active) { this.active = active; return this; }

        public Employee build() {
            return new Employee(id, name, phone, dailyWageRate, joinDate, active);
        }
    }
}
