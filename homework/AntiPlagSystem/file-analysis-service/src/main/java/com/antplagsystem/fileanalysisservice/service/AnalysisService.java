package com.antplagsystem.fileanalysisservice.service;

import com.antplagsystem.fileanalysisservice.model.Report;
import com.antplagsystem.fileanalysisservice.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AnalysisService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Report analyzeWork(Long workId) {
        try {
            // 1. Запрашиваем текущую работу
            String storageUrl = "http://file-storing-service:8081";
            Map<String, Object> currentWork = restTemplate.getForObject(
                    storageUrl + "/api/works/" + workId,
                    Map.class
            );

            if (currentWork == null) {
                throw new RuntimeException("Работа #" + workId + " не найдена");
            }

            String currentTaskId = (String) currentWork.get("taskId");
            String currentContent = (String) currentWork.get("content");
            LocalDateTime currentTime = parseTime(currentWork.get("submissionTime"));

            // 2. Запрашиваем ВСЕ работы по этому заданию
            List<Map<String, Object>> allWorks = restTemplate.getForObject(
                    storageUrl + "/api/works/task/" + currentTaskId,
                    List.class
            );

            if (allWorks == null) {
                allWorks = Collections.emptyList();
            }

            // 3. Проверяем на плагиат
            boolean plagiarismDetected = false;
            String details = "✅ Плагиат не обнаружен. Работа уникальна.";
            Long plagiarizedFromId = null;

            for (Map<String, Object> otherWork : allWorks) {
                Long otherId = Long.valueOf(otherWork.get("id").toString());
                String otherContent = (String) otherWork.get("content");
                LocalDateTime otherTime = parseTime(otherWork.get("submissionTime"));

                if (otherId.equals(workId)) continue;

                String cleanCurrent = cleanText(currentContent);
                String cleanOther = cleanText(otherContent);

                if (cleanCurrent.equals(cleanOther) && otherTime.isBefore(currentTime)) {
                    plagiarismDetected = true;
                    plagiarizedFromId = otherId;
                    details = String.format(
                            "⚠️ ОБНАРУЖЕН ПЛАГИАТ!\n" +
                                    "Работа #%d (сдана %s) скопирована с работы #%d (сдана %s)\n" +
                                    "Текст полностью совпадает.",
                            workId, formatTime(currentTime),
                            otherId, formatTime(otherTime)
                    );
                    break;
                }
            }

            // 4. Сохраняем отчет в БД
            Report report = new Report();
            report.setWorkId(workId);
            report.setStatus("COMPLETED");
            report.setPlagiarismDetected(plagiarismDetected);
            report.setDetails(details);
            report.setAnalysisTime(LocalDateTime.now());

            if (plagiarizedFromId != null) {
                report.setPlagiarizedFromWorkId(plagiarizedFromId);
            }

            return reportRepository.save(report);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка анализа: " + e.getMessage(), e);
        }
    }

    public List<Report> getReportsByWorkId(Long workId) {
        return reportRepository.findByWorkId(workId);
    }

    // Вспомогательные методы (остаются без изменений)
    private String cleanText(String text) {
        if (text == null) return "";
        return text.trim()
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("\\s+", " ");
    }

    private LocalDateTime parseTime(Object timeObj) {
        try {
            if (timeObj instanceof String timeStr) {
                String clean = timeStr.replace("Z", "").split("\\.")[0];
                return LocalDateTime.parse(clean);
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга времени: " + timeObj);
        }
        return LocalDateTime.now();
    }

    private String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}