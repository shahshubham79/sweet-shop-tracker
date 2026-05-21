package com.sweetshop.tracker.dto;

import com.sweetshop.tracker.model.Settlement;
import java.math.BigDecimal;
import java.util.List;

public class DashboardStats {
    private Long totalActiveEmployees;
    private BigDecimal totalOutstandingAdvances;
    private Integer totalUnpaidDaysWorked;
    private BigDecimal totalUnpaidEarned;
    private List<Settlement> recentSettlements;

    // Constructors
    public DashboardStats() {}

    public DashboardStats(Long totalActiveEmployees, BigDecimal totalOutstandingAdvances,
                          Integer totalUnpaidDaysWorked, BigDecimal totalUnpaidEarned,
                          List<Settlement> recentSettlements) {
        this.totalActiveEmployees = totalActiveEmployees;
        this.totalOutstandingAdvances = totalOutstandingAdvances;
        this.totalUnpaidDaysWorked = totalUnpaidDaysWorked;
        this.totalUnpaidEarned = totalUnpaidEarned;
        this.recentSettlements = recentSettlements;
    }

    // Getters and Setters
    public Long getTotalActiveEmployees() { return totalActiveEmployees; }
    public void setTotalActiveEmployees(Long totalActiveEmployees) { this.totalActiveEmployees = totalActiveEmployees; }

    public BigDecimal getTotalOutstandingAdvances() { return totalOutstandingAdvances; }
    public void setTotalOutstandingAdvances(BigDecimal totalOutstandingAdvances) { this.totalOutstandingAdvances = totalOutstandingAdvances; }

    public Integer getTotalUnpaidDaysWorked() { return totalUnpaidDaysWorked; }
    public void setTotalUnpaidDaysWorked(Integer totalUnpaidDaysWorked) { this.totalUnpaidDaysWorked = totalUnpaidDaysWorked; }

    public BigDecimal getTotalUnpaidEarned() { return totalUnpaidEarned; }
    public void setTotalUnpaidEarned(BigDecimal totalUnpaidEarned) { this.totalUnpaidEarned = totalUnpaidEarned; }

    public List<Settlement> getRecentSettlements() { return recentSettlements; }
    public void setRecentSettlements(List<Settlement> recentSettlements) { this.recentSettlements = recentSettlements; }

    // Builder Pattern
    public static DashboardStatsBuilder builder() {
        return new DashboardStatsBuilder();
    }

    public static class DashboardStatsBuilder {
        private Long totalActiveEmployees;
        private BigDecimal totalOutstandingAdvances;
        private Integer totalUnpaidDaysWorked;
        private BigDecimal totalUnpaidEarned;
        private List<Settlement> recentSettlements;

        public DashboardStatsBuilder totalActiveEmployees(Long totalActiveEmployees) { this.totalActiveEmployees = totalActiveEmployees; return this; }
        public DashboardStatsBuilder totalOutstandingAdvances(BigDecimal totalOutstandingAdvances) { this.totalOutstandingAdvances = totalOutstandingAdvances; return this; }
        public DashboardStatsBuilder totalUnpaidDaysWorked(Integer totalUnpaidDaysWorked) { this.totalUnpaidDaysWorked = totalUnpaidDaysWorked; return this; }
        public DashboardStatsBuilder totalUnpaidEarned(BigDecimal totalUnpaidEarned) { this.totalUnpaidEarned = totalUnpaidEarned; return this; }
        public DashboardStatsBuilder recentSettlements(List<Settlement> recentSettlements) { this.recentSettlements = recentSettlements; return this; }

        public DashboardStats build() {
            return new DashboardStats(totalActiveEmployees, totalOutstandingAdvances, totalUnpaidDaysWorked, totalUnpaidEarned, recentSettlements);
        }
    }
}
