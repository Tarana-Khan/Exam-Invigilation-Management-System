package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;
    private LocalDate examDate;
    private String slot; // Morning/Evening

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
}