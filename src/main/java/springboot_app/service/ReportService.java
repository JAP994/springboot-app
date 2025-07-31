package springboot_app.service;

import springboot_app.domain.Report;
import springboot_app.dto.ReportUpdateFieldDTO;

import java.util.List;

public interface ReportService {
    Report saveReport(Report report, String ip, String userAgent);
    List<Report> getAllReports();
    Report getByReportNumber(String reportNumber);
    Report updateReport(String reportNumber, Report updatedReport, String ip, String userAgent);
    Report updatePartial(String reportNumber, ReportUpdateFieldDTO dto);
    void deleteReport(String reportNumber);
}
