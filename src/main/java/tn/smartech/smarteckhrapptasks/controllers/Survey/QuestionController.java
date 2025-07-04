package tn.smartech.smarteckhrapptasks.controllers.Survey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.smartech.smarteckhrapptasks.entities.Survey.Question;
import tn.smartech.smarteckhrapptasks.services.Survey.QuestionService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/question")
@CrossOrigin
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public List<Question> getAll() {
        return questionService.getAll();
    }

    @GetMapping("/{id}")
    public Question getById(@PathVariable Long id) {
        return questionService.getById(id);
    }

    @GetMapping("/survey/{surveyId}")
    public List<Question> getBySurvey(@PathVariable Long surveyId) {
        return questionService.getBySurveyId(surveyId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Question create(@RequestBody Question question) {
        return questionService.create(question);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Question update(@PathVariable Long id, @RequestBody Question question) {
        return questionService.update(id, question);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        questionService.delete(id);
    }
}

