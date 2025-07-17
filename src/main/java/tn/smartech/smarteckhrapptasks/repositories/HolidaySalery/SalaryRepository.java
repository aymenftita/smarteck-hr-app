package tn.smartech.smarteckhrapptasks.repositories.HolidaySalery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Salary.Salary;

@Repository
public interface SalaryRepository extends JpaRepository<Salary,Long> {
}
