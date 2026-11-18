package com.exam.entity;

import jakarta.persistence.*;

@Entity
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityId;

    @ManyToOne
    private Faculty faculty;

    @ManyToOne
    private Examination exam;
    
    private String status; // Available/Unavailable

    public Long getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(Long availabilityId) { this.availabilityId = availabilityId; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    public Examination getExam() { return exam; }
    public void setExam(Examination exam) { this.exam = exam; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}