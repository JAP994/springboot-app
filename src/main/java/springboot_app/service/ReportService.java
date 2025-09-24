package springboot_app.service;

import springboot_app.domain.Report;
import springboot_app.dto.ReportUpdateFieldDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    Report saveReport(Report report, String ip, String userAgent);
    Report updateReport(String reportNumber, Report report, String ip, String userAgent);
    Report updatePartial(String reportNumber, ReportUpdateFieldDTO dto, String ip, String userAgent);
    Report getByReportNumber(String reportNumber);
    List<Report> getAllReports();
    void deleteReportByNumber(String reportNumber);

    // Método para paginación
    Page<Report> getReports(Pageable pageable);
}
