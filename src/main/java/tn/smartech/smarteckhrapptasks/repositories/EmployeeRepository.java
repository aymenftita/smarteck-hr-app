package tn.smartech.smarteckhrapptasks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Employee;
import tn.smartech.smarteckhrapptasks.entities.RoleType;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByEmail(String email);
    Optional<Employee> findByEmail(String email);
    List<Employee> findByRole(RoleType role);

    @Query("SELECT t FROM Employee t WHERE t.email = :email")
    Optional<Employee> getEmployeeByEmail(String email);


}
