package com.example.todo.model;

public class Task {
    private Long id;
    private String description;
    private boolean completed;
    private Long userId; // References the User who created the task

    // Constructors
    public Task() {
    }

    public Task(Long id, String description, boolean completed, Long userId) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
