package com.taskmanager.model;

public class AiGenerateRequest {

    private String goal;
    private String mode;

    public AiGenerateRequest() {}

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
