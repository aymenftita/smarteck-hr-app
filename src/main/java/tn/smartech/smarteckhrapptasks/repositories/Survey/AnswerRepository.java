package tn.smartech.smarteckhrapptasks.repositories.Survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Survey.Answer;
import tn.smartech.smarteckhrapptasks.entities.Survey.SurveyChartDTO;

import java.util.List;


@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a.question.id, a.question.text, AVG(a.rating) " +
            "FROM Answer a WHERE a.response.survey.id = :surveyId GROUP BY a.question.id, a.question.text")
    List<Object[]> findAverageRatingsBySurvey(@Param("surveyId") Long surveyId);

    @Query("SELECT a.question.id, a.question.text, COUNT(a) " +
            "FROM Answer a WHERE a.response.survey.id = :surveyId GROUP BY a.question.id, a.question.text")
    List<Object[]> findResponseCountsBySurvey(@Param("surveyId") Long surveyId);


    @Query("SELECT NEW tn.smartech.smarteckhrapptasks.entities.Survey.SurveyChartDTO(" +
            "q.id, q.text, AVG(a.rating), COUNT(a)) " +
            "FROM Answer a JOIN a.question q " +
            "WHERE a.response.survey.id = :surveyId " +
            "GROUP BY q.id, q.text")
    List<SurveyChartDTO> findSurveyChartData(@Param("surveyId") Long surveyId);

    @Query("SELECT a.response.id, a.question.id, a.rating " +
            "FROM Answer a " +
            "WHERE a.response.survey.id = :surveyId")
    List<Object[]> findRawAnswerData(@Param("surveyId") Long surveyId);
}
