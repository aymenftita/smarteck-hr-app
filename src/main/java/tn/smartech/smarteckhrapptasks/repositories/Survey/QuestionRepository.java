package tn.smartech.smarteckhrapptasks.repositories.Survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Survey.Question;


import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findBySurveyId(Long surveyId);
}
