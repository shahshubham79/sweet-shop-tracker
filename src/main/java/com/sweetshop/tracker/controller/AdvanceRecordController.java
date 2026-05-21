package com.sweetshop.tracker.controller;

import com.sweetshop.tracker.model.AdvanceRecord;
import com.sweetshop.tracker.model.Employee;
import com.sweetshop.tracker.repository.AdvanceRecordRepository;
import com.sweetshop.tracker.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/advances")
@CrossOrigin(origins = "*")
public class AdvanceRecordController {

    @Autowired
    private AdvanceRecordRepository advanceRecordRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/employee/{employeeId}/active")
    public List<AdvanceRecord> getActiveAdvances(@PathVariable Long employeeId) {
        return advanceRecordRepository.findByEmployeeIdAndSettled(employeeId, false);
    }

    @PostMapping
    public ResponseEntity<?> createAdvance(@RequestBody AdvanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElse(null);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee not found with id: " + request.getEmployeeId());
        }

        AdvanceRecord advance = AdvanceRecord.builder()
                .employee(employee)
                .date(request.getDate())
                .amount(request.getAmount())
                .remarks(request.getRemarks())
                .settled(false)
                .build();

        return ResponseEntity.ok(advanceRecordRepository.save(advance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdvance(@PathVariable Long id) {
        return advanceRecordRepository.findById(id).map(advance -> {
            if (advance.getSettled()) {
                return ResponseEntity.badRequest().body("Cannot delete a settled advance record.");
            }
            advanceRecordRepository.delete(advance);
            return ResponseEntity.ok().body("Advance record deleted successfully.");
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class AdvanceRequest {
        private Long employeeId;
        private LocalDate date;
        private BigDecimal amount;
        private String remarks;

        // Getters and Setters
        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
}
