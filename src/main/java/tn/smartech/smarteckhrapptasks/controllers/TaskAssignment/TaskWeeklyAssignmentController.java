package tn.smartech.smarteckhrapptasks.controllers.TaskAssignment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import tn.smartech.smarteckhrapptasks.entities.*;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;
import tn.smartech.smarteckhrapptasks.repositories.EmployeeRepository;
import tn.smartech.smarteckhrapptasks.repositories.TaskRepository;
import tn.smartech.smarteckhrapptasks.repositories.WeeklyAssignmentRepository;
import tn.smartech.smarteckhrapptasks.repositories.WeeklyScoreRepository;
import tn.smartech.smarteckhrapptasks.services.TaskAssignment.TaskWeeklyAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/task-assignment")
@CrossOrigin("*")
public class TaskWeeklyAssignmentController {

    private final TaskWeeklyAssignmentService service;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final WeeklyAssignmentRepository weeklyAssignmentRepository;
    private final WeeklyScoreRepository weeklyScoreRepository;
    public TaskWeeklyAssignmentController(TaskWeeklyAssignmentService service, TaskRepository taskRepository, EmployeeRepository employeeRepository, WeeklyAssignmentRepository assignmentRepository, WeeklyScoreRepository weeklyScoreRepository) {
        this.service = service;
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
        this.weeklyAssignmentRepository = assignmentRepository;

        this.weeklyScoreRepository = weeklyScoreRepository;
    }

    @GetMapping("/employees/{role}")
    public List<Employee> getEmployeesByRole(@PathVariable RoleType role) {
        return service.getEmployeesByRole(role);
    }

    @GetMapping("/weekly-assignments/employee/{employeeId}")
    public ResponseEntity<List<WeeklyAssignment>> getWeeklyAssignmentsByEmployee(
            @PathVariable int employeeId) {

        List<WeeklyAssignment> assignments = weeklyAssignmentRepository.findByEmployeeId(employeeId);

        if (assignments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(assignments);
    }

    @PostMapping("/assign/{employeeId}")
    @Transactional
    public ResponseEntity<?> assignTasks(
            @PathVariable int employeeId,
            @RequestBody List<Task> tasks,
            Authentication authentication) {

        // 1. Get the currently logged-in user (assigner)
        String currentUserEmail = authentication.getName();
        Employee assignedBy = employeeRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // 2. Get the employee who will receive the tasks
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        // 3. Validate tasks
        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException("At least one task must be provided");
        }

        // 4. Get or create weekly assignment for current week
        LocalDate today = LocalDate.now();
        WeeklyAssignment weeklyAssignment = weeklyAssignmentRepository
                .findByEmployeeAndDateRange(employee.getId(),
                        today.with(DayOfWeek.MONDAY),
                        today.with(DayOfWeek.SUNDAY))
                .orElseGet(() -> {
                    WeeklyAssignment newAssignment = new WeeklyAssignment();
                    newAssignment.setEmployee(employee);
                    newAssignment.setStartDate(today.with(DayOfWeek.MONDAY));
                    newAssignment.setEndDate(today.with(DayOfWeek.SUNDAY));
                    newAssignment.setTotalWorkDaysOfWeek(0);
                    newAssignment.setTasks(new ArrayList<>());
                    return weeklyAssignmentRepository.save(newAssignment);
                });

        // Calculate total requested work days
        int totalRequestedDays = tasks.stream()
                .mapToInt(Task::getDaysOfWork)
                .sum();

        // Check if adding these tasks would exceed the 5-day limit
        if (weeklyAssignment.getTotalWorkDaysOfWeek() + totalRequestedDays > 5) {
            return ResponseEntity.badRequest().body(
                    "Cannot assign tasks. Current work days: " + weeklyAssignment.getTotalWorkDaysOfWeek() +
                            ", Requested additional days: " + totalRequestedDays +
                            ". Maximum 5 work days per week allowed.");
        }

        // 5. Process and save tasks
        List<Task> savedTasks = new ArrayList<>();
        int totalDaysAdded = 0;

        for (Task taskRequest : tasks) {
            if (taskRequest.getDaysOfWork() < 1 || taskRequest.getDaysOfWork() > 5) {
                throw new IllegalArgumentException("Days of work must be between 1 and 5");
            }

            Task task = new Task();
            task.setDescription(taskRequest.getDescription());
            task.setEmployee(employee);
            task.setAssignedBy(assignedBy);
            task.setDaysOfWork(taskRequest.getDaysOfWork());
            task.setStatus(TaskStatus.PENDING);
            task.setWeeklyAssignment(weeklyAssignment);

            Task savedTask = taskRepository.save(task);
            savedTasks.add(savedTask);
            totalDaysAdded += task.getDaysOfWork();
        }

        // 6. Update weekly assignment
        weeklyAssignment.setTotalWorkDaysOfWeek(weeklyAssignment.getTotalWorkDaysOfWeek() + totalDaysAdded);
        weeklyAssignment.getTasks().addAll(savedTasks);
        WeeklyAssignment updatedAssignment = weeklyAssignmentRepository.save(weeklyAssignment);

        return ResponseEntity.ok(updatedAssignment);
    }

    @PostMapping("/reassign-reported")
    public void reassignReportedTasks() {
        service.reassignReportedTasks();
    }

    @PostMapping("/evaluate/{operatorId}/{weeklyAssignmentId}")
    public ResponseEntity<WeeklyScore> evaluateEmployee(
            @PathVariable int operatorId,
            @PathVariable int weeklyAssignmentId,
            Authentication authentication) {  // Get authentication from security context

        // Get the currently logged-in evaluator
        String currentUserEmail = authentication.getName();
        Employee evaluatedBy = employeeRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Evaluator not found"));

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
        weeklyScore.setEvaluatedBy(evaluatedBy);  // Set the evaluator from current session

        WeeklyScore savedScore = weeklyScoreRepository.save(weeklyScore);
        return ResponseEntity.ok(savedScore);
    }

    @GetMapping("/tasks/weekly-assignment/{weeklyAssignmentId}")
    public ResponseEntity<List<Task>> getTasksByWeeklyAssignment(
            @PathVariable int weeklyAssignmentId) {

        // Verify weekly assignment exists
        if (!weeklyAssignmentRepository.existsById(weeklyAssignmentId)) {
            return ResponseEntity.notFound().build();
        }

        List<Task> tasks = taskRepository.findByWeeklyAssignmentId(weeklyAssignmentId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable int taskId,
            @RequestParam TaskStatus status,
            Authentication authentication) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Optional: Verify the current user has permission to update this task
        String currentUserEmail = authentication.getName();
        Employee currentUser = employeeRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getRole().equals(RoleType.ADMIN)) {
            throw new RuntimeException("Only admins can update task status");
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/scores/week/{weeklyAssignmentId}")
    public ResponseEntity<WeeklyScore> getScoreByWeeklyAssignment(
            @PathVariable int weeklyAssignmentId,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();
        Employee operator = employeeRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        WeeklyAssignment week = weeklyAssignmentRepository.findById(weeklyAssignmentId)
                .orElseThrow(() -> new RuntimeException("Weekly assignment not found"));

        WeeklyScore score = weeklyScoreRepository.findByOperatorAndWeek(operator, week)
                .orElseThrow(() -> new RuntimeException("Score not found"));

        return ResponseEntity.ok(score);
    }

    @GetMapping("/tasks/{employeeEmail}")
    public List<Task> getTasksByEmployeeEmail(@PathVariable String employeeEmail) {
        return taskRepository.findByEmail(employeeEmail);
    }

    @PostMapping("/assign-existing-task/{taskId}/{newEmployeeId}")
    @Transactional
    public ResponseEntity<Task> assignExistingTaskToEmployee(
            @PathVariable int taskId,
            @PathVariable int newEmployeeId,
            Authentication authentication) {

        // 1. Get the currently logged-in user (assigner)
        String currentUserEmail = authentication.getName();
        Employee assignedBy = employeeRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // 2. Get the original task
        Task originalTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // 3. Get the new employee
        Employee newEmployee = employeeRepository.findById(newEmployeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + newEmployeeId));

        // 4. Get or create weekly assignment for current week for the new employee
        LocalDate today = LocalDate.now();
        WeeklyAssignment weeklyAssignment = weeklyAssignmentRepository
                .findByEmployeeAndDateRange(newEmployee.getId(),
                        today.with(DayOfWeek.MONDAY),
                        today.with(DayOfWeek.SUNDAY))
                .orElseGet(() -> {
                    WeeklyAssignment newAssignment = new WeeklyAssignment();
                    newAssignment.setEmployee(newEmployee);
                    newAssignment.setStartDate(today.with(DayOfWeek.MONDAY));
                    newAssignment.setEndDate(today.with(DayOfWeek.SUNDAY));
                    newAssignment.setTotalWorkDaysOfWeek(0);
                    newAssignment.setTasks(new ArrayList<>());
                    return weeklyAssignmentRepository.save(newAssignment);
                });

        // 5. Check if adding this task would exceed the 5-day limit
        if (weeklyAssignment.getTotalWorkDaysOfWeek() + originalTask.getDaysOfWork() > 5) {
            throw new IllegalArgumentException("Cannot assign task. Maximum 5 work days per week allowed.");
        }

        // 6. Create a new task (copy of the original)
        Task newTask = new Task();
        newTask.setDescription(originalTask.getDescription());
        newTask.setEmployee(newEmployee);
        newTask.setAssignedBy(assignedBy);
        newTask.setDaysOfWork(originalTask.getDaysOfWork());
        newTask.setStatus(TaskStatus.PENDING);
        newTask.setWeeklyAssignment(weeklyAssignment);

        // 7. Save the new task
        Task savedTask = taskRepository.save(newTask);

        // 8. Update weekly assignment
        weeklyAssignment.setTotalWorkDaysOfWeek(weeklyAssignment.getTotalWorkDaysOfWeek() + newTask.getDaysOfWork());
        weeklyAssignment.getTasks().add(savedTask);
        weeklyAssignmentRepository.save(weeklyAssignment);

        return ResponseEntity.ok(savedTask);
    }
}