package tn.smartech.smarteckhrapptasks.services.TaskAssignment;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.TaskStatus;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.repositories.EmployeeRepository;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.TaskRepository;
import tn.smartech.smarteckhrapptasks.repositories.TaskAssignment.WeeklyAssignmentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeeklyAssignmentService {
    @Autowired
    private WeeklyAssignmentRepository weeklyAssignmentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public WeeklyAssignment createWeeklyAssignment(WeeklyAssignment assignment) {
        if (assignment.getStartDate() == null || assignment.getEndDate() == null) {
            throw new RuntimeException("Start and end dates are required");
        }
        if (assignment.getEndDate().isBefore(assignment.getStartDate().plusDays(6))) {
            throw new RuntimeException("End date must be 6 days after start date");
        }
        assignment.setTotalWorkDaysOfWeek(0);
        return weeklyAssignmentRepository.save(assignment);
    }

    @Transactional
    public WeeklyAssignment addTaskToAssignment(int assignmentId, Task task) {
        WeeklyAssignment assignment = weeklyAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Weekly assignment not found"));

        int newTotalDays = (int) (assignment.getTotalWorkDaysOfWeek() + task.getDaysOfWork());
        if (newTotalDays > 5) {
            throw new RuntimeException("Cannot add task: Exceeds 5-day limit");
        }

        task.setWeeklyAssignment(assignment);
        task.setStatus(TaskStatus.PENDING);
        assignment.getTasks().add(task);
        assignment.setTotalWorkDaysOfWeek(newTotalDays);

        taskRepository.save(task);
        return weeklyAssignmentRepository.save(assignment);
    }

    @Transactional
    public void reassignReportedTasks(int weeklyAssignmentId) {
        WeeklyAssignment currentAssignment = weeklyAssignmentRepository.findById(weeklyAssignmentId)
                .orElseThrow(() -> new RuntimeException("Weekly assignment not found"));

        List<Task> reportedTasks = taskRepository.findByWeeklyAssignmentIdAndStatus(
                weeklyAssignmentId, TaskStatus.REPORTED);

        if (reportedTasks.isEmpty()) {
            return;
        }

        LocalDate nextWeekStart = currentAssignment.getEndDate().plusDays(1);
        LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
        WeeklyAssignment nextAssignment = weeklyAssignmentRepository
                .findByEmployeeIdAndStartDate(currentAssignment.getEmployee().getId(), nextWeekStart)
                .orElseGet(() -> {
                    WeeklyAssignment newAssignment = new WeeklyAssignment(
                            currentAssignment.getEmployee(),
                            nextWeekStart,
                            nextWeekEnd,
                            new ArrayList<>(),
                            0
                    );
                    return weeklyAssignmentRepository.save(newAssignment);
                });

        for (Task reportedTask : reportedTasks) {
            int newTotalDays = (int) (nextAssignment.getTotalWorkDaysOfWeek() + reportedTask.getDaysOfWork());
            if (newTotalDays > 5) {
                throw new RuntimeException("Cannot reassign task: Exceeds 5-day limit for next week");
            }

            Task newTask = new Task(
                    reportedTask.getDescription(),
                    reportedTask.getEmployee(),
                    (int) reportedTask.getDaysOfWork(),
                    TaskStatus.PENDING,
                    reportedTask.getAssignedBy()
            );

            nextAssignment.getTasks().add(newTask);
            nextAssignment.setTotalWorkDaysOfWeek(newTotalDays);
            taskRepository.save(newTask);
        }

        weeklyAssignmentRepository.save(nextAssignment);
    }

    public List<WeeklyAssignment> findByEmployeeId(Integer employeeId) {
        return weeklyAssignmentRepository.findByEmployeeIdWithTasks(employeeId);
    }
}