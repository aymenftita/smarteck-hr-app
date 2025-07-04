package tn.smartech.smarteckhrapptasks.services.Survey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.smartech.smarteckhrapptasks.entities.Survey.Survey;
import tn.smartech.smarteckhrapptasks.repositories.Survey.SurveyRepository;


import java.util.List;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    public List<Survey> getAll() {
        return surveyRepository.findAll();
    }

    public Survey getById(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
    }

    public Survey create(Survey survey) {
        return surveyRepository.save(survey);
    }

    public Survey update(Long id, Survey updated) {
        Survey survey = getById(id);
        survey.setTitle(updated.getTitle());
        survey.setDescription(updated.getDescription());
        return surveyRepository.save(survey);
    }

    public void delete(Long id) {
        surveyRepository.deleteById(id);
    }
}
