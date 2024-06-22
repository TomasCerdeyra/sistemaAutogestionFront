/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupoAutogestion.sistemaautogestion.model;

import java.time.LocalDateTime;

/**
 *
 * @author tomas
 */
public class UserEvent {
    private String studentId;
    private String courseId;
    private String event;
    private LocalDateTime timestamp;
    
    // Constructor
    public UserEvent(String studentId, String courseId, String event) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.event = event;
        this.timestamp = LocalDateTime.now(); //Para establecer el tiempo actual 
    }
    
    // Getters y setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
