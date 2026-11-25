package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DutyAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;

    @ManyToOne
    private Faculty faculty;

    @ManyToOne
    private Examination exam;

    @ManyToOne
    private Classroom classroom;
    
    private String slot;
    private LocalDateTime allocationTime = LocalDateTime.now();

    public Long getAllocationId() { return allocationId; }
    public void setAllocationId(Long allocationId) { this.allocationId = allocationId; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    public Examination getExam() { return exam; }
    public void setExam(Examination exam) { this.exam = exam; }
    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    public LocalDateTime getAllocationTime() { return allocationTime; }
    public void setAllocationTime(LocalDateTime allocationTime) { this.allocationTime = allocationTime; }
}
