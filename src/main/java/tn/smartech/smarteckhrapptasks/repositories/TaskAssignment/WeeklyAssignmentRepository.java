package tn.smartech.smarteckhrapptasks.repositories.TaskAssignment;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Employee;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyAssignmentRepository extends JpaRepository<WeeklyAssignment, Integer> {
    Optional<WeeklyAssignment> findByEmployeeIdAndStartDate(Integer employeeId, LocalDate startDate);
    List<WeeklyAssignment> findByEmployeeId(Integer employeeId);
    List<WeeklyAssignment> findByStartDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT wa FROM WeeklyAssignment wa JOIN FETCH wa.tasks WHERE wa.employee.id = :employeeId")
    List<WeeklyAssignment> findByEmployeeIdWithTasks(@Param("employeeId") Integer employeeId);

    List<WeeklyAssignment> findByEmployee(Employee employee);

    Optional<WeeklyAssignment> findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Employee employee, LocalDate endDate, LocalDate startDate);

    @Query("SELECT wa FROM WeeklyAssignment wa " +
            "WHERE wa.employee.id = :employeeId " +
            "AND wa.startDate <= :endDate " +
            "AND wa.endDate >= :startDate")
    Optional<WeeklyAssignment> findByEmployeeAndDateRange(
            @Param("employeeId") int employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

