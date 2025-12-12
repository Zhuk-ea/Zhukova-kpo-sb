package com.antplagsystem.fileanalysisservice.controller;

import com.antplagsystem.fileanalysisservice.model.Report;
import com.antplagsystem.fileanalysisservice.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class AnalysisController {
    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/analyze/{workId}")
    public Map<String, Object> analyzeWork(@PathVariable Long workId) {
        try {
            Report report = analysisService.analyzeWork(workId);

            Map<String, Object> response = new HashMap<>();
            response.put("reportId", report.getId());
            response.put("status", report.getStatus());
            response.put("plagiarismDetected", report.getPlagiarismDetected());
            response.put("message", "Analysis completed successfully");
            response.put("details", report.getDetails());

            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("reportId", null);
            error.put("status", "ERROR");
            error.put("message", e.getMessage());
            error.put("plagiarismDetected", false);
            return error;
        }
    }

    @GetMapping("/work/{workId}")
    public List<Report> getReportsByWork(@PathVariable Long workId) {
        return analysisService.getReportsByWorkId(workId);
    }
}