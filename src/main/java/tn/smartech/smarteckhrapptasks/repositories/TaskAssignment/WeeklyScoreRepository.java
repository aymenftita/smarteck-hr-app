package tn.smartech.smarteckhrapptasks.repositories.TaskAssignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Employee;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyAssignment;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyScoreRepository extends JpaRepository<WeeklyScore, Integer> {

    List<WeeklyScore> findByOperatorId(Integer operatorId);
    List<WeeklyScore> findByWeekId(Integer weeklyAssignmentId);
    List<WeeklyScore> findByEvaluatedById(Integer supervisorId);

    Optional<WeeklyScore> findByOperatorAndWeek(Employee operator, WeeklyAssignment week);
}
