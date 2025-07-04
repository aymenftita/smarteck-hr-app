package tn.smartech.smarteckhrapptasks.controllers.TaskAssignment;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;
import tn.smartech.smarteckhrapptasks.services.TaskAssignment.TaskService;
import tn.smartech.smarteckhrapptasks.services.TaskAssignment.WeeklyAssignmentService;
import tn.smartech.smarteckhrapptasks.services.TaskAssignment.WeeklyScoreService;

import java.util.List;

@RestController
@RequestMapping("/api/weekly-assignments")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowedHeaders = "*", exposedHeaders = {"Authorization", "Content-Type"}, allowCredentials = "true")
public class WeeklyAssignmentController {

    @Autowired
    private WeeklyAssignmentService weeklyAssignmentService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WeeklyScoreService weeklyScoreService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<WeeklyAssignment> createWeeklyAssignment(@Valid @RequestBody WeeklyAssignment assignment) {
        WeeklyAssignment createdAssignment = weeklyAssignmentService.createWeeklyAssignment(assignment);
        return ResponseEntity.ok(createdAssignment);
    }

    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<WeeklyAssignment> addTaskToAssignment(@PathVariable int id, @Valid @RequestBody Task task) {
        WeeklyAssignment updatedAssignment = weeklyAssignmentService.addTaskToAssignment(id, task);
        return ResponseEntity.ok(updatedAssignment);
    }

    @PostMapping("/{id}/reassign")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Void> reassignReportedTasks(@PathVariable int id) {
        weeklyAssignmentService.reassignReportedTasks(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Task> assignTask(@Valid @RequestBody Task task, @RequestParam int weeklyAssignmentId) {
        Task assignedTask = taskService.assignTask(task, weeklyAssignmentId);
        return ResponseEntity.ok(assignedTask);
    }

    @PutMapping("/tasks/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable int id, @RequestBody TaskStatus status) {
        Task updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}/reported-tasks")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<List<Task>> getReportedTasks(@PathVariable int id) {
        List<Task> reportedTasks = taskService.getReportedTasks(id);
        return ResponseEntity.ok(reportedTasks);
    }

    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<WeeklyScore> evaluateOperator(@PathVariable int id, @RequestBody EvaluateRequest request) {
        WeeklyScore score = weeklyScoreService.evaluateOperator(id, request.getScore(), request.getEvaluatedById());
        return ResponseEntity.ok(score);
    }

    @GetMapping("/{id}/score")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<Double> calculateWeeklyScore(@PathVariable int id) {
        double score = weeklyScoreService.calculateWeeklyScore(id);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    public ResponseEntity<List<WeeklyAssignment>> getAssignmentsByEmployee(@PathVariable int employeeId) {
        List<WeeklyAssignment> assignments = weeklyAssignmentService.findByEmployeeId(employeeId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        return taskService.getTasks();
    }

    static class EvaluateRequest {
        private double score;
        private int evaluatedById;

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getEvaluatedById() {
            return evaluatedById;
        }

        public void setEvaluatedById(int evaluatedById) {
            this.evaluatedById = evaluatedById;
        }
    }

}