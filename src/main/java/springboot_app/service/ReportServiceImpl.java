package springboot_app.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import springboot_app.domain.Report;
import springboot_app.dto.ReportUpdateFieldDTO;
import springboot_app.repository.ReportRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Report saveReport(Report report, String ip, String userAgent) {
        int year = report.getDetectedDateTime().getYear();
        int count = reportRepository.countByDetectedLocationUnitAndYear(report.getDetectedLocationUnit(), year);
        String sequential = String.format("%04d", count + 1);
        String reportNumber = "REP-" + report.getDetectedLocationUnit() + "-" + year + "-" + sequential;

        report.setReportNumber(reportNumber);
        report.setRegistrationIp(ip);
        report.setUserAgent(userAgent);
        report.setCreationDate(LocalDateTime.now());

        return reportRepository.save(report);
    }

    @Override
    @Transactional
    public Report updateReport(String reportNumber, Report report, String ip, String userAgent) {
        Report existing = getByReportNumber(reportNumber);
        if (existing == null) throw new EntityNotFoundException("Report not found");

        existing.setDetectedDateTime(report.getDetectedDateTime());
        existing.setDetectedLocationUnit(report.getDetectedLocationUnit());
        existing.setInvolvedMaterialPersonnel(report.getInvolvedMaterialPersonnel());
        existing.setDetailedDescription(report.getDetailedDescription());
        existing.setEvidenceFile(report.getEvidenceFile());

        existing.setLastModifiedIp(ip);
        existing.setLastModifiedUserAgent(userAgent);
        existing.setLastModifiedDate(LocalDateTime.now());

        return reportRepository.save(existing);
    }

    @Override
    public Report updatePartial(String reportNumber, ReportUpdateFieldDTO dto, String ip, String userAgent) {
        Report existing = getByReportNumber(reportNumber);
        if (existing == null) throw new EntityNotFoundException("Report not found");

        if (dto.getDetectedLocationUnit() != null) existing.setDetectedLocationUnit(dto.getDetectedLocationUnit());
        if (dto.getDetailedDescription() != null) existing.setDetailedDescription(dto.getDetailedDescription());
        // Agrega más campos si es necesario

        existing.setLastModifiedIp(ip);
        existing.setLastModifiedUserAgent(userAgent);
        existing.setLastModifiedDate(LocalDateTime.now());

        return reportRepository.save(existing);
    }

    @Override
    public Report getByReportNumber(String reportNumber) {
        return reportRepository.findByReportNumber(reportNumber).orElse(null);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteReportByNumber(String reportNumber) {
        reportRepository.deleteByReportNumber(reportNumber);
    }
} 
