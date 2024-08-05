package com.grupoAutogestion.sistemaautogestion.model;

import java.util.List;

public class Course {
    
    private String courseId;
    private String courseName;
    private String description;
    private List<String> students; // Agregar el campo students

    public Course() {
    }

    public Course(String courseName, String description, List<String> students) {
        this.courseName = courseName;
        this.description = description;
        this.students = students;
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

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }
}
