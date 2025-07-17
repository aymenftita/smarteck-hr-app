package tn.smartech.smarteckhrapptasks.controllers.HolidaySalery;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tn.smartech.smarteckhrapptasks.entities.Employee;
import tn.smartech.smarteckhrapptasks.entities.Salary.HolidayRequest;
import tn.smartech.smarteckhrapptasks.repositories.HolidaySalery.HolidayRequestRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/holiday")
@CrossOrigin
public class HolidayRequestController {

    private final HolidayRequestRepository holidayRequestRepository;

    public HolidayRequestController(HolidayRequestRepository holidayRequestRepository) {
        this.holidayRequestRepository = holidayRequestRepository;
    }

    @PostMapping
    public ResponseEntity<HolidayRequest> createHoliday(@Valid @RequestBody HolidayRequest holiday) throws Exception {
        HolidayRequest createdHoliday = holidayRequestRepository.save(holiday);
        return ResponseEntity.ok(createdHoliday);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteHoliday(@PathVariable int id) {
        holidayRequestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<HolidayRequest>> getAllHolidays() {
        List<HolidayRequest> holidays = holidayRequestRepository.findAll();
        return ResponseEntity.ok(holidays);
    }
}
