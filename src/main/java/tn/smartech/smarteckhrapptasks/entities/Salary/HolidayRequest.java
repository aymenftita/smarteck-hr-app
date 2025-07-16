package tn.smartech.smarteckhrapptasks.entities.Salary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import tn.smartech.smarteckhrapptasks.entities.Employee;

@Entity
public class HolidayRequest {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
