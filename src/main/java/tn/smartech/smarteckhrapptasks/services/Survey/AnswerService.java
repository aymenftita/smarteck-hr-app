package tn.smartech.smarteckhrapptasks.services.Survey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.smartech.smarteckhrapptasks.entities.Survey.Answer;
import tn.smartech.smarteckhrapptasks.repositories.Survey.AnswerRepository;


import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    public List<Answer> getAll() {
        return answerRepository.findAll();
    }

    public Answer getById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
    }

    public Answer create(Answer answer) {
        return answerRepository.save(answer);
    }

    public Answer update(Long id, Answer updated) {
        Answer answer = getById(id);
        answer.setRating(updated.getRating());
        answer.setQuestion(updated.getQuestion());
        answer.setResponse(updated.getResponse());
        return answerRepository.save(answer);
    }

    public void delete(Long id) {
        answerRepository.deleteById(id);
    }
}
