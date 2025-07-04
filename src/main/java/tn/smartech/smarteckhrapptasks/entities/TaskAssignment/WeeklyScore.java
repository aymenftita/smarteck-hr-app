package tn.smartech.smarteckhrapptasks.entities.TaskAssignment;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import tn.smartech.smarteckhrapptasks.entities.Employee;

@Setter
@Getter
@Entity
@Builder
public class WeeklyScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Employee operator;

    @OneToOne
    private WeeklyAssignment week;

    @Min(0)
    @Max(100)
    private double score;

    @ManyToOne
    @JoinColumn(name = "evaluated_by")
    private Employee evaluatedBy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Employee getOperator() {
        return operator;
    }

    public void setOperator(Employee operator) {
        this.operator = operator;
    }

    public WeeklyAssignment getWeek() {
        return week;
    }

    public void setWeek(WeeklyAssignment week) {
        this.week = week;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Employee getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(Employee evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public WeeklyScore(int id, Employee operator, WeeklyAssignment week, double score, Employee evaluatedBy) {
        this.id = id;
        this.operator = operator;
        this.week = week;
        this.score = score;
        this.evaluatedBy = evaluatedBy;
    }

    public WeeklyScore() {
    }

    public WeeklyScore(Employee operator, double score, Employee evaluatedBy) {
        this.operator = operator;
        this.score = score;
        this.evaluatedBy = evaluatedBy;
    }
}