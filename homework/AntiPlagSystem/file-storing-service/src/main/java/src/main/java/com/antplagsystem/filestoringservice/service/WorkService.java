package src.main.java.com.antplagsystem.filestoringservice.service;

import src.main.java.com.antplagsystem.filestoringservice.model.Work;
import src.main.java.com.antplagsystem.filestoringservice.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkService {

    @Autowired
    private WorkRepository workRepository;

    public Work createWork(String studentName, String taskId, String fileName, String content) {
        Work work = new Work(studentName, taskId, fileName, content);
        work.setSubmissionTime(LocalDateTime.now());
        return workRepository.save(work);
    }

    public List<Work> getAllWorks() {
        return workRepository.findAll();
    }

    public Work getWorkById(Long id) {
        return workRepository.findById(id).orElse(null);
    }

    public List<Work> getWorksByTask(String taskId) {
        return workRepository.findByTaskIdOrderBySubmissionTimeAsc(taskId);
    }
}