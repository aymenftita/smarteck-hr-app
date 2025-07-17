package tn.smartech.smarteckhrapptasks.entities.Salary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import tn.smartech.smarteckhrapptasks.entities.Employee;

import java.util.List;

@Entity
public class Salary {
    @Id
    private Long id;

    @OneToOne
    private Employee employee;

    private Double salary;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
