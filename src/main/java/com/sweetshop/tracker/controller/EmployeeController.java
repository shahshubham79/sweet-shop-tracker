package com.sweetshop.tracker.controller;

import com.sweetshop.tracker.model.Employee;
import com.sweetshop.tracker.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByActiveTrueOrderByNameAsc();
    }

    @GetMapping("/all")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setName(employeeDetails.getName());
            employee.setPhone(employeeDetails.getPhone());
            employee.setDailyWageRate(employeeDetails.getDailyWageRate());
            employee.setJoinDate(employeeDetails.getJoinDate());
            if (employeeDetails.getActive() != null) {
                employee.setActive(employeeDetails.getActive());
            }
            return ResponseEntity.ok(employeeRepository.save(employee));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<Employee> toggleActive(@PathVariable Long id) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setActive(!employee.getActive());
            return ResponseEntity.ok(employeeRepository.save(employee));
        }).orElse(ResponseEntity.notFound().build());
    }
}
