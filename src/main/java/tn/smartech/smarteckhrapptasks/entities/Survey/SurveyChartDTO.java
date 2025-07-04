package tn.smartech.smarteckhrapptasks.entities.Survey;

public class SurveyChartDTO {
    private Long questionId;
    private String questionText;
    private Double averageRating;
    private Long responseCount;

    // Constructor, getters and setters
    public SurveyChartDTO(Long questionId, String questionText, Double averageRating, Long responseCount) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.averageRating = averageRating;
        this.responseCount = responseCount;
    }
}
