package tn.smartech.smarteckhrapptasks.services.TaskAssignment;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.smartech.smarteckhrapptasks.entities.*;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.repositories.*;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.TaskRepository;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.WeeklyAssignmentRepository;


import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private WeeklyAssignmentRepository weeklyAssignmentRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public Task assignTask(Task task, int weeklyAssignmentId) {
        WeeklyAssignment assignment = weeklyAssignmentRepository.findById(weeklyAssignmentId)
                .orElseThrow(() -> new RuntimeException("Weekly assignment not found"));

        // Validate 5-day limit
        int newTotalDays = (int) (assignment.getTotalWorkDaysOfWeek() + task.getDaysOfWork());
        if (newTotalDays > 5) {
            throw new RuntimeException("Cannot assign task: Exceeds 5-day limit");
        }

        Employee operator = employeeRepository.findById(task.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Operator not found"));
        Employee supervisor = employeeRepository.findById(task.getAssignedBy().getId())
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        if (supervisor.getRole() != RoleType.SUPERVISOR) {
            throw new RuntimeException("AssignedBy must be a supervisor");
        }

        task.setStatus(TaskStatus.PENDING);
        task.setWeeklyAssignment(assignment);
        task.setEmployee(operator);
        task.setAssignedBy(supervisor);

        assignment.setTotalWorkDaysOfWeek(newTotalDays);
        weeklyAssignmentRepository.save(assignment);
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(int taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public List<Task> getReportedTasks(int weeklyAssignmentId) {
        return taskRepository.findByWeeklyAssignmentIdAndStatus(weeklyAssignmentId, TaskStatus.REPORTED);
    }

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public List<Task> findTasksByEmployeeId(int employeeId) {
        return taskRepository.findTaskByEmployee(employeeId);
    }
}