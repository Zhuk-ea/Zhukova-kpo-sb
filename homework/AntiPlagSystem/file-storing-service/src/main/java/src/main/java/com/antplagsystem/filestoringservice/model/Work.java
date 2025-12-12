package src.main.java.com.antplagsystem.filestoringservice.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "submission_time", nullable = false)
    private LocalDateTime submissionTime;

    // Конструкторы
    public Work() {}

    public Work(String studentName, String taskId, String fileName, String content) {
        this.studentName = studentName;
        this.taskId = taskId;
        this.fileName = fileName;
        this.content = content;
        this.submissionTime = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSubmissionTime() { return submissionTime; }
    public void setSubmissionTime(LocalDateTime submissionTime) { this.submissionTime = submissionTime; }
}