package springboot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_app.domain.Report;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    Optional<Report> findByReportNumber(String reportNumber);
}
