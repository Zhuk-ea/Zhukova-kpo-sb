package com.antplagsystem.fileanalysisservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "plagiarized_from_work_id")
    private Long plagiarizedFromWorkId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "plagiarism_detected", nullable = false)
    private Boolean plagiarismDetected;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "analysis_time", nullable = false)
    private LocalDateTime analysisTime;

    // Конструкторы
    public Report() {}

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public Long getPlagiarizedFromWorkId() { return plagiarizedFromWorkId; }
    public void setPlagiarizedFromWorkId(Long plagiarizedFromWorkId) { this.plagiarizedFromWorkId = plagiarizedFromWorkId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getPlagiarismDetected() { return plagiarismDetected; }
    public void setPlagiarismDetected(Boolean plagiarismDetected) { this.plagiarismDetected = plagiarismDetected; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(LocalDateTime analysisTime) { this.analysisTime = analysisTime; }
}