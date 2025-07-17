package tn.smartech.smarteckhrapptasks.repositories.HolidaySalery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Salary.HolidayRequest;

@Repository
public interface HolidayRequestRepository extends JpaRepository<HolidayRequest, Integer> {
}
