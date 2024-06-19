package com.grupoAutogestion.sistemaautogestion.model;

public class Course {
    
    private Long id;
    private String courseName;
    private String description;

    public Course() {
    }

    public Course(String courseName, String description) {
        this.courseName = courseName;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
