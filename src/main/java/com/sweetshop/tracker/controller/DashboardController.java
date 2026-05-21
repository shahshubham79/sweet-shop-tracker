package com.sweetshop.tracker.controller;

import com.sweetshop.tracker.dto.DashboardStats;
import com.sweetshop.tracker.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private SettlementService settlementService;

    @GetMapping("/stats")
    public DashboardStats getStats() {
        return settlementService.getDashboardStats();
    }
}
