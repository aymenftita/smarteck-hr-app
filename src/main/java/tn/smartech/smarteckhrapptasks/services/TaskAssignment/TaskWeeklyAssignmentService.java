// TaskWeeklyAssignmentService.java
package tn.smartech.smarteckhrapptasks.services.TaskAssignment;

import tn.smartech.smarteckhrapptasks.entities.*;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;
import tn.smartech.smarteckhrapptasks.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskWeeklyAssignmentService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final WeeklyAssignmentRepository weeklyAssignmentRepository;
    private final WeeklyScoreRepository weeklyScoreRepository;

    public TaskWeeklyAssignmentService(EmployeeRepository employeeRepository,
                                       TaskRepository taskRepository,
                                       WeeklyAssignmentRepository weeklyAssignmentRepository,
                                       WeeklyScoreRepository weeklyScoreRepository) {
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        this.weeklyAssignmentRepository = weeklyAssignmentRepository;
        this.weeklyScoreRepository = weeklyScoreRepository;
    }

    // Get employees by role
    public List<Employee> getEmployeesByRole(RoleType role) {
        return employeeRepository.findByRole(role);
    }

    // Assign tasks to an employee with automatic weekly assignment creation
    @Transactional
    public WeeklyAssignment assignTasksToEmployee(int employeeId, List<Task> tasks, Employee assignedBy) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Get current week's assignment or create a new one
        LocalDate today = LocalDate.now();
        WeeklyAssignment weeklyAssignment = getOrCreateWeeklyAssignment(employee, today);

        // Validate and assign tasks
        for (Task task : tasks) {
            if (task.getDaysOfWork() < 1 || task.getDaysOfWork() > 5) {
                throw new IllegalArgumentException("Days of work must be between 1 and 5");
            }

            task.setEmployee(employee);
            task.setAssignedBy(assignedBy);
            task.setStatus(TaskStatus.PENDING);
            task.setWeeklyAssignment(weeklyAssignment);

            // Update total work days
            weeklyAssignment.setTotalWorkDaysOfWeek(
                    weeklyAssignment.getTotalWorkDaysOfWeek() + task.getDaysOfWork());
        }

        // Save tasks and update weekly assignment
        taskRepository.saveAll(tasks);
        return weeklyAssignmentRepository.save(weeklyAssignment);
    }

    // Automatically reassign reported tasks to the next week
    @Transactional
    public void reassignReportedTasks() {
        List<Task> reportedTasks = taskRepository.findByStatus(TaskStatus.REPORTED);

        for (Task task : reportedTasks) {
            Employee employee = task.getEmployee();
            LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

            WeeklyAssignment nextWeekAssignment = getOrCreateWeeklyAssignment(employee, nextMonday);

            task.setWeeklyAssignment(nextWeekAssignment);
            task.setStatus(TaskStatus.PENDING);

            nextWeekAssignment.setTotalWorkDaysOfWeek(
                    nextWeekAssignment.getTotalWorkDaysOfWeek() + task.getDaysOfWork());

            taskRepository.save(task);
            weeklyAssignmentRepository.save(nextWeekAssignment);
        }
    }

    // Evaluate employee and calculate weekly score
    @Transactional
    public WeeklyScore evaluateEmployee(int operatorId, int weeklyAssignmentId, Employee evaluatedBy) {
        Employee operator = employeeRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        WeeklyAssignment week = weeklyAssignmentRepository.findById(weeklyAssignmentId)
                .orElseThrow(() -> new RuntimeException("Weekly assignment not found"));

        List<Task> tasks = taskRepository.findByWeeklyAssignmentId(weeklyAssignmentId);

        if (tasks.isEmpty()) {
            throw new RuntimeException("No tasks found for this weekly assignment");
        }

        // Calculate score based on completed tasks
        long completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();

        double score = (completedTasks * 100.0) / tasks.size();

        // Save or update the score
        WeeklyScore weeklyScore = weeklyScoreRepository.findByOperatorAndWeek(operator, week)
                .orElse(new WeeklyScore());

        weeklyScore.setOperator(operator);
        weeklyScore.setWeek(week);
        weeklyScore.setScore(score);
        weeklyScore.setEvaluatedBy(evaluatedBy);

        return weeklyScoreRepository.save(weeklyScore);
    }

    // Helper method to get or create weekly assignment
    private WeeklyAssignment getOrCreateWeeklyAssignment(Employee employee, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return weeklyAssignmentRepository
                .findByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employee, endOfWeek, startOfWeek)
                .orElseGet(() -> {
                    WeeklyAssignment newAssignment = new WeeklyAssignment();
                    newAssignment.setEmployee(employee);
                    newAssignment.setStartDate(startOfWeek);
                    newAssignment.setEndDate(endOfWeek);
                    newAssignment.setTotalWorkDaysOfWeek(0);
                    newAssignment.setTasks(new ArrayList<>());
                    return weeklyAssignmentRepository.save(newAssignment);
                });
    }
}