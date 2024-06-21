package com.grupoAutogestion.sistemaautogestion.model;

public class Course {
    
    private String courseId;
    private String courseName;
    private String description;

    public Course() {
    }

    public Course(String courseName, String description) {
        this.courseName = courseName;
        this.description = description;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
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
