package tn.smartech.smarteckhrapptasks.entities.TaskAssignment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import tn.smartech.smarteckhrapptasks.entities.Employee;

@Setter
@Getter
@Entity
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Min(1)
    private float daysOfWork;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private Employee assignedBy;

    @ManyToOne
    @JoinColumn(name = "weekly_assignment_id")
    private WeeklyAssignment weeklyAssignment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public float getDaysOfWork() {
        return daysOfWork;
    }

    public void setDaysOfWork(float daysOfWork) {
        this.daysOfWork = daysOfWork;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Employee getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Employee assignedBy) {
        this.assignedBy = assignedBy;
    }

    public WeeklyAssignment getWeeklyAssignment() {
        return weeklyAssignment;
    }

    public void setWeeklyAssignment(WeeklyAssignment weeklyAssignment) {
        this.weeklyAssignment = weeklyAssignment;
    }

    public Task(int id, String description, Employee employee, int daysOfWork, TaskStatus status, Employee assignedBy, WeeklyAssignment weeklyAssignment) {
        this.id = id;
        this.description = description;
        this.employee = employee;
        this.daysOfWork = daysOfWork;
        this.status = status;
        this.assignedBy = assignedBy;
        this.weeklyAssignment = weeklyAssignment;
    }

    public Task(String description, Employee employee, int daysOfWork, TaskStatus status, Employee assignedBy) {
        this.description = description;
        this.employee = employee;
        this.daysOfWork = daysOfWork;
        this.status = status;
        this.assignedBy = assignedBy;
    }

    public Task() {
    }
}