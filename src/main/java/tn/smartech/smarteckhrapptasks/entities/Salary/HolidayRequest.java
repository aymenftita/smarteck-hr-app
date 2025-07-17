package tn.smartech.smarteckhrapptasks.entities.Salary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import tn.smartech.smarteckhrapptasks.entities.Employee;

import java.time.LocalDate;

@Entity
public class HolidayRequest {
    @Id
    private Long id;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @ManyToOne
    private Employee employee;

    private HolidayRequestStatus status;

    private boolean consulted;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public HolidayRequestStatus getStatus() {
        return status;
    }

    public void setStatus(HolidayRequestStatus status) {
        this.status = status;
    }

    public boolean isConsulted() {
        return consulted;
    }

    public void setConsulted(boolean consulted) {
        this.consulted = consulted;
    }
}
