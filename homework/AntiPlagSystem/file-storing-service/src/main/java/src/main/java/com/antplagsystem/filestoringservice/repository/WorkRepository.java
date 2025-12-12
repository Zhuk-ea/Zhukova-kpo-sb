package src.main.java.com.antplagsystem.filestoringservice.repository;

import src.main.java.com.antplagsystem.filestoringservice.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByTaskIdOrderBySubmissionTimeAsc(String taskId);
}