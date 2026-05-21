package com.sweetshop.tracker.controller;

import com.sweetshop.tracker.model.Employee;
import com.sweetshop.tracker.model.WorkRecord;
import com.sweetshop.tracker.repository.EmployeeRepository;
import com.sweetshop.tracker.repository.WorkRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work")
@CrossOrigin(origins = "*")
public class WorkRecordController {

    @Autowired
    private WorkRecordRepository workRecordRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/employee/{employeeId}/active")
    public List<WorkRecord> getActiveWorkRecords(@PathVariable Long employeeId) {
        return workRecordRepository.findByEmployeeIdAndSettled(employeeId, false);
    }

    @PostMapping
    public ResponseEntity<?> createWorkRecord(@RequestBody WorkRecordRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElse(null);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee not found with id: " + request.getEmployeeId());
        }

        WorkRecord record = WorkRecord.builder()
                .employee(employee)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .daysWorked(request.getDaysWorked())
                .remarks(request.getRemarks())
                .settled(false)
                .build();

        return ResponseEntity.ok(workRecordRepository.save(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkRecord(@PathVariable Long id) {
        return workRecordRepository.findById(id).map(record -> {
            if (record.getSettled()) {
                return ResponseEntity.badRequest().body("Cannot delete a settled work record.");
            }
            workRecordRepository.delete(record);
            return ResponseEntity.ok().body("Work record deleted successfully.");
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class WorkRecordRequest {
        private Long employeeId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer daysWorked;
        private String remarks;

        // Getters and Setters
        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public Integer getDaysWorked() { return daysWorked; }
        public void setDaysWorked(Integer daysWorked) { this.daysWorked = daysWorked; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
}
