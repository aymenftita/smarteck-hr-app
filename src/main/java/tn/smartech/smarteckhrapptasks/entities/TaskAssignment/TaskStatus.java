package tn.smartech.smarteckhrapptasks.entities.TaskAssignment;

public enum TaskStatus {
    PENDING("Pending", 0),
    STARTED_25("25% Complete", 25),
    STARTED_50("50% Complete", 50),
    STARTED_75("75% Complete", 75),
    COMPLETED("Completed", 100),
    REPORTED("Reported", -1);

    private final String displayName;
    private final int percentage;

    TaskStatus(String displayName, int percentage) {
        this.displayName = displayName;
        this.percentage = percentage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPercentage() {
        return percentage;
    }

    // Optional: Helper method to get status from percentage
    public static TaskStatus fromPercentage(int percentage) {
        for (TaskStatus status : values()) {
            if (status.percentage == percentage) {
                return status;
            }
        }
        return PENDING; // Default if no match
    }
}
