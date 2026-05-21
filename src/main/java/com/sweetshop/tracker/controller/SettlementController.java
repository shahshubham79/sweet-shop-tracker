package com.sweetshop.tracker.controller;

import com.sweetshop.tracker.dto.SettlementSummary;
import com.sweetshop.tracker.model.Settlement;
import com.sweetshop.tracker.repository.SettlementRepository;
import com.sweetshop.tracker.service.SettlementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@CrossOrigin(origins = "*")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementRepository settlementRepository;

    @GetMapping("/employee/{employeeId}/summary")
    public ResponseEntity<SettlementSummary> getSettlementSummary(@PathVariable Long employeeId) {
        try {
            return ResponseEntity.ok(settlementService.calculateSummary(employeeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<?> recordSettlement(@PathVariable Long employeeId, @RequestBody SettlementRequest request) {
        try {
            Settlement settlement = settlementService.recordSettlement(employeeId, request.getRemarks());
            return ResponseEntity.ok(settlement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public List<Settlement> getSettlementHistory() {
        return settlementRepository.findAllByOrderBySettlementDateDesc();
    }

    @GetMapping("/employee/{employeeId}/history")
    public List<Settlement> getEmployeeSettlementHistory(@PathVariable Long employeeId) {
        return settlementRepository.findByEmployeeIdOrderBySettlementDateDesc(employeeId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Settlement> getSettlementById(@PathVariable Long id) {
        return settlementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public static class SettlementRequest {
        private String remarks;

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
