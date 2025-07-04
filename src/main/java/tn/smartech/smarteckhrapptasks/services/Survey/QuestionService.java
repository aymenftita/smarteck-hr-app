package tn.smartech.smarteckhrapptasks.services.Survey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.smartech.smarteckhrapptasks.entities.Survey.Question;
import tn.smartech.smarteckhrapptasks.entities.Survey.Survey;
import tn.smartech.smarteckhrapptasks.entities.Survey.SurveyChartDTO;
import tn.smartech.smarteckhrapptasks.repositories.Survey.AnswerRepository;
import tn.smartech.smarteckhrapptasks.repositories.Survey.QuestionRepository;
import tn.smartech.smarteckhrapptasks.repositories.Survey.SurveyRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    public Question getById(Long id) {
        return questionRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public List<Question> getBySurveyId(Long surveyId) {
        return questionRepository.findBySurveyId(surveyId);
    }

    public Question create(Question question) {
        return questionRepository.save(question);
    }

    public Question update(Long id, Question updated) {
        Question question = getById(id);
        question.setText(updated.getText());
        return questionRepository.save(question);
    }

    public void delete(Long id) {
        questionRepository.deleteById(Math.toIntExact(id));
    }



}
