package springboot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_app.domain.Report;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    Optional<Report> findByReportNumber(String reportNumber);

    void deleteByReportNumber(String reportNumber);

    @Query("SELECT COUNT(r.id) FROM Report r WHERE r.detectedLocationUnit = :unit AND EXTRACT(YEAR FROM r.detectedDateTime) = :year")
    int countByDetectedLocationUnitAndYear(@Param("unit") String unit, @Param("year") int year);
}
