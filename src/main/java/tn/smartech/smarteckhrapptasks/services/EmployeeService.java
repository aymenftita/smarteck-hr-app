package tn.smartech.smarteckhrapptasks.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smartech.smarteckhrapptasks.entities.Employee;
import tn.smartech.smarteckhrapptasks.entities.RoleType;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;
import tn.smartech.smarteckhrapptasks.repositories.EmployeeRepository;
import tn.smartech.smarteckhrapptasks.repositories.WeeklyScoreRepository;



import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (employee.getRole() == null) {
            throw new RuntimeException("Role is required");
        }
        employee.setPassword(new BCryptPasswordEncoder().encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    public List<WeeklyScore> getScoresByEmployee(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return weeklyScoreRepository.findByOperatorId(employeeId);
    }


    @Transactional
    public Employee updateEmployee(int id, Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!existingEmployee.getEmail().equals(employee.getEmail()) &&
                employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            existingEmployee.setPassword(new BCryptPasswordEncoder().encode(employee.getPassword()));
        }
        existingEmployee.setRole(employee.getRole());
        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public void deleteEmployee(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!weeklyScoreRepository.findByOperatorId(employee.getId()).isEmpty() ||
                !employee.getTasks().isEmpty()) {
            throw new RuntimeException("Cannot delete employee with associated tasks or scores");
        }
        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }


    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByRole(RoleType role) {
        return employeeRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.getEmployeeByEmail(email);
    }
}