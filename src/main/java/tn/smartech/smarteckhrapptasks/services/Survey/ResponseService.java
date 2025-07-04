package tn.smartech.smarteckhrapptasks.services.Survey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.smartech.smarteckhrapptasks.entities.Survey.Response;
import tn.smartech.smarteckhrapptasks.entities.Survey.Survey;
import tn.smartech.smarteckhrapptasks.entities.Survey.SurveyChartDTO;
import tn.smartech.smarteckhrapptasks.repositories.Survey.AnswerRepository;
import tn.smartech.smarteckhrapptasks.repositories.Survey.ResponseRepository;
import tn.smartech.smarteckhrapptasks.repositories.Survey.SurveyRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    public List<Response> getAll() {
        return responseRepository.findAll();
    }

    public Response getById(Long id) {
        return responseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Response not found"));
    }

    public Response create(Response response) {
        if (response.getAnswers() != null) {
            response.getAnswers().forEach(answer -> answer.setResponse(response));
        }
        return responseRepository.save(response);
    }

    public void delete(Long id) {
        responseRepository.deleteById(id);
    }

    public Map<String, Object> getSurveySummary(Long surveyId) {
        Map<String, Object> summary = new HashMap<>();

        // Get average ratings per question
        List<Object[]> avgRatings = answerRepository.findAverageRatingsBySurvey(surveyId);
        summary.put("avgRatings", avgRatings);

        // Get response counts per question
        List<Object[]> responseCounts = answerRepository.findResponseCountsBySurvey(surveyId);
        summary.put("responseCounts", responseCounts);

        return summary;
    }

    public Map<String, Object> getSurveyResultsForDashboard(Long surveyId) {
        Map<String, Object> results = new HashMap<>();

        // 1. Get chart data
        List<SurveyChartDTO> chartData = answerRepository.findSurveyChartData(surveyId);
        results.put("chartData", chartData);

        // 2. Get raw answer data for table
        List<Object[]> rawAnswers = answerRepository.findRawAnswerData(surveyId);
        results.put("rawAnswers", rawAnswers);

        // 3. Get survey details
        Survey survey = surveyRepository.findById(surveyId).orElseThrow();
        results.put("survey", survey);

        return results;
    }
}

