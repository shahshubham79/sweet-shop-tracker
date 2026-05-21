package com.sweetshop.tracker.repository;

import com.sweetshop.tracker.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByActive(Boolean active);
    List<Employee> findByActiveTrueOrderByNameAsc();
}
