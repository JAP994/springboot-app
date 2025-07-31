package springboot_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springboot_app.domain.Report;
import springboot_app.dto.ReportUpdateFieldDTO;
import springboot_app.repository.ReportRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Report saveReport(Report report, String ip, String userAgent) {
        report.setRegistrationIp(ip);
        report.setUserAgent(userAgent);
        report.setCreationDate(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Report getByReportNumber(String reportNumber) {
        Optional<Report> optional = reportRepository.findByReportNumber(reportNumber);
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("Reporte no encontrado con número: " + reportNumber);
        }
        return optional.get();
    }

@Override
public Report updateReport(String reportNumber, Report updatedReport, String ip, String userAgent) {
    Report existingReport = getByReportNumber(reportNumber);

    existingReport.setDetectedDateTime(updatedReport.getDetectedDateTime());
    existingReport.setDetectedLocationUnit(updatedReport.getDetectedLocationUnit());
    existingReport.setInvolvedMaterialPersonnel(updatedReport.getInvolvedMaterialPersonnel());
    existingReport.setDetailedDescription(updatedReport.getDetailedDescription());
    existingReport.setEvidenceFile(updatedReport.getEvidenceFile());

    existingReport.setLastModifiedIp(ip);
    existingReport.setLastModifiedUserAgent(userAgent);
    existingReport.setLastModifiedDate(LocalDateTime.now());

    return reportRepository.save(existingReport);
}

    @Override
    public Report updatePartial(String reportNumber, ReportUpdateFieldDTO dto) {
        Report existingReport = getByReportNumber(reportNumber);

        // Ejemplo, actualizar solo los campos no nulos del DTO
        if (dto.getDetectedDateTime() != null) {
            existingReport.setDetectedDateTime(dto.getDetectedDateTime());
        }
        if (dto.getDetectedLocationUnit() != null) {
            existingReport.setDetectedLocationUnit(dto.getDetectedLocationUnit());
        }
        if (dto.getInvolvedMaterialPersonnel() != null) {
            existingReport.setInvolvedMaterialPersonnel(dto.getInvolvedMaterialPersonnel());
        }
        if (dto.getDetailedDescription() != null) {
            existingReport.setDetailedDescription(dto.getDetailedDescription());
        }
        // Puedes agregar más campos según dto

        return reportRepository.save(existingReport);
    }

    @Override
    public void deleteReport(String reportNumber) {
        Report existingReport = getByReportNumber(reportNumber);
        reportRepository.delete(existingReport);
    }
}
