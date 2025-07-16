package tn.smartech.smarteckhrapptasks.repositories.TaskAssignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByWeeklyAssignmentId(Integer weeklyAssignmentId);
    @Query("SELECT t FROM Task t WHERE t.weeklyAssignment.id = :weeklyAssignmentId AND t.status = :status")
    List<Task> findByWeeklyAssignmentIdAndStatus(@Param("weeklyAssignmentId") Integer weeklyAssignmentId, @Param("status") TaskStatus status);
    List<Task> findByEmployeeIdAndWeeklyAssignmentId(Integer employeeId, Integer weeklyAssignmentId);

    @Query("SELECT t FROM Task t WHERE t.employee.id = :EmployeeId")
    List<Task> findTaskByEmployee(@Param("EmployeeId") Integer EmployeeId);

    List<Task> findByStatus(TaskStatus status);
    List<Task> findByEmployeeIdAndStatus(int employeeId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.weeklyAssignment.id = :weeklyAssignmentId")
    List<Task> findByWeeklyAssignmentId(@Param("weeklyAssignmentId") int weeklyAssignmentId);

    @Query("SELECT t FROM Task t WHERE t.employee.email = :email")
    List<Task> findByEmail(@Param("email") String email);
}
