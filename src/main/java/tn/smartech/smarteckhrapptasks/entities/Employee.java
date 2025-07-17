package tn.smartech.smarteckhrapptasks.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.smartech.smarteckhrapptasks.entities.Survey.Response;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.Task;
import tn.smartech.smarteckhrapptasks.entities.TaskAssignment.WeeklyScore;


import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private String photo;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "operator")
    @JsonIgnore
    private List<WeeklyScore> scores = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Response> responses;

    public Employee(List<Response> responses, List<WeeklyScore> scores, List<Task> tasks, String photo, RoleType role, String password, String email, String name, int id) {
        this.responses = responses;
        this.scores = scores;
        this.tasks = tasks;
        this.photo = photo;
        this.role = role;
        this.password = password;
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<WeeklyScore> getScores() {
        return scores;
    }

    public void setScores(List<WeeklyScore> scores) {
        this.scores = scores;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
}