package tn.smartech.smarteckhrapptasks.services.TaskAssignment;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.smartech.smarteckhrapptasks.entities.*;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;
import tn.smartech.smarteckhrapptasks.repositories.*;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.TaskRepository;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.WeeklyAssignmentRepository;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.WeeklyScoreRepository;

import java.util.List;


@Service
public class WeeklyScoreService {
    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;
    @Autowired
    private WeeklyAssignmentRepository weeklyAssignmentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public WeeklyScore evaluateOperator(int weeklyAssignmentId, double score, int evaluatedById) {
        WeeklyAssignment assignment = weeklyAssignmentRepository.findById(weeklyAssignmentId)
                .orElseThrow(() -> new RuntimeException("Weekly assignment not found"));
        Employee supervisor = employeeRepository.findById(evaluatedById)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        if (supervisor.getRole() != RoleType.SUPERVISOR) {
            throw new RuntimeException("Evaluator must be a supervisor");
        }

        WeeklyScore weeklyScore = new WeeklyScore(
                assignment.getEmployee(),
                score,
                supervisor
        );

        return weeklyScoreRepository.save(weeklyScore);
    }

    public double calculateWeeklyScore(int weeklyAssignmentId) {
        List<Task> tasks = taskRepository.findByWeeklyAssignmentId(weeklyAssignmentId);
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();
        long totalTasks = tasks.size();
        return totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100 : 0;
    }
}
