package com.antplagsystem.fileanalysisservice.repository;

import com.antplagsystem.fileanalysisservice.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByWorkId(Long workId);
}