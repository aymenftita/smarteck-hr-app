package tn.smartech.smarteckhrapptasks.entities.TaskAssignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.*;
import tn.smartech.smarteckhrapptasks.entities.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
public class WeeklyAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @OneToMany(mappedBy = "weeklyAssignment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    @Min(0)
    @Max(5)
    private int TotalWorkDaysOfWeek;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int getTotalWorkDaysOfWeek() {
        return TotalWorkDaysOfWeek;
    }

    public void setTotalWorkDaysOfWeek(int totalWorkDaysOfWeek) {
        TotalWorkDaysOfWeek = totalWorkDaysOfWeek;
    }

    public WeeklyAssignment(int id, Employee employee, LocalDate startDate, LocalDate endDate, List<Task> tasks, int totalWorkDaysOfWeek) {
        this.id = id;
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = tasks;
        TotalWorkDaysOfWeek = totalWorkDaysOfWeek;
    }

    public WeeklyAssignment(Employee employee, LocalDate startDate, LocalDate endDate, List<Task> tasks, int totalWorkDaysOfWeek) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = tasks;
        this.TotalWorkDaysOfWeek = totalWorkDaysOfWeek;
    }

    public WeeklyAssignment() {
    }
}