package tn.smartech.smarteckhrapptasks.controllers.Survey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.smartech.smarteckhrapptasks.entities.Survey.Response;
import tn.smartech.smarteckhrapptasks.services.Survey.ResponseService;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/response")
@CrossOrigin
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @GetMapping
    public List<Response> getAll() {
        return responseService.getAll();
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable Long id) {
        return responseService.getById(id);
    }

    @PostMapping
    public Response create(@RequestBody Response response) {
        return responseService.create(response);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        responseService.delete(id);
    }

    @GetMapping("/summary/{surveyId}")
    public Map<String, Object> getSurveySummary(@PathVariable Long surveyId) {
        return responseService.getSurveySummary(surveyId);
    }

    @GetMapping("/dashboard/{surveyId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable Long surveyId) {
        return ResponseEntity.ok(responseService.getSurveyResultsForDashboard(surveyId));
    }
}

