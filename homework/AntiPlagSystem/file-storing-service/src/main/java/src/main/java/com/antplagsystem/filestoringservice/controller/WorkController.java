package src.main.java.com.antplagsystem.filestoringservice.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import src.main.java.com.antplagsystem.filestoringservice.model.Work;
import src.main.java.com.antplagsystem.filestoringservice.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/works")
public class WorkController {
    @Autowired
    private WorkService workService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public Map<String, Object> createWork(@RequestBody Map<String, String> request) {
        try {
            Work work = workService.createWork(
                    request.get("studentName"),
                    request.get("taskId"),
                    request.get("fileName"),
                    request.get("content")
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", work.getId());
            response.put("message", "Work submitted successfully");
            response.put("submissionTime", work.getSubmissionTime().toString());

            // Запускаем анализ асинхронно
            triggerAnalysisAsync(work.getId());

            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to submit work: " + e.getMessage());
            return error;
        }
    }

    @Async
    public void triggerAnalysisAsync(Long workId) {
        try {
            String analysisUrl = "http://file-analysis-service:8082";
            String url = analysisUrl + "/api/reports/analyze/" + workId;
            restTemplate.postForObject(url, null, Map.class);
            System.out.println("Анализ для работы #" + workId + " запущен");
        } catch (Exception e) {
            System.err.println("Ошибка при вызове анализа для работы #" + workId + ": " + e.getMessage());
        }
    }

    @GetMapping
    public List<Work> getAllWorks() {
        return workService.getAllWorks();
    }

    @GetMapping("/{id}")
    public Work getWork(@PathVariable Long id) {
        return workService.getWorkById(id);
    }

    @GetMapping("/task/{taskId}")
    public List<Work> getWorksByTask(@PathVariable String taskId) {
        return workService.getWorksByTask(taskId);
    }

    @GetMapping("/test")
    public String test() {
        return "✅ File Storing Service работает! Всего работ: " +
                workService.getAllWorks().size();
    }
}