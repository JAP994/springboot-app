package springboot_app.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import springboot_app.domain.Report;
import springboot_app.dto.ReportUpdateFieldDTO;
import springboot_app.service.ReportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService service;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> createWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("detectedDateTime") String detectedDateTime,
            @RequestParam("detectedLocationUnit") String detectedLocationUnit,
            @RequestParam("involvedMaterialPersonnel") String involvedMaterialPersonnel,
            @RequestParam("detailedDescription") String detailedDescription,
            HttpServletRequest request
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Debe proporcionar un archivo válido.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return ResponseEntity.badRequest().body("El archivo debe tener un nombre válido.");
        }

        String filenameLower = originalFilename.toLowerCase();
        if (!filenameLower.endsWith(".pdf") && !filenameLower.endsWith(".jpg") && !filenameLower.endsWith(".jpeg")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF o JPG.");
        }

        // Validación de fecha y hora
        LocalDateTime detectedDateTimeParsed = null;
        String[] patterns = {"dd/MM/yyyy HH:mm", "dd/MM/yyyy"};
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                detectedDateTimeParsed = LocalDateTime.parse(detectedDateTime, formatter);
                break;
            } catch (Exception ignored) {}
        }

        if (detectedDateTimeParsed == null) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use dd/MM/yyyy HH:mm.");
        }

        if (detectedDateTimeParsed.isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha y hora detectada no puede ser mayor que la fecha y hora actual.");
        }

        // Construcción de ruta de archivo
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String baseUploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + datePath;

        File uploadDir = new File(baseUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String newFilename = UUID.randomUUID() + "_" + originalFilename;
        File destinationFile = new File(uploadDir, newFilename);
        file.transferTo(destinationFile);
        String relativePath = "uploads/" + datePath + "/" + newFilename;

        // Captura de IP y user-agent
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Report report = new Report();
        report.setDetectedDateTime(detectedDateTimeParsed);
        report.setDetectedLocationUnit(detectedLocationUnit);
        report.setInvolvedMaterialPersonnel(involvedMaterialPersonnel);
        report.setDetailedDescription(detailedDescription);
        report.setEvidenceFile(relativePath);

        Report created = service.saveReport(report, ip, userAgent);

        return ResponseEntity.ok(created);
    }

    
    @GetMapping
    public List<Report> getAll() {
        return service.getAllReports();
    }

    @GetMapping("/{reportNumber}")
    public ResponseEntity<?> getOne(@PathVariable String reportNumber) {
        Report report = service.getByReportNumber(reportNumber);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PutMapping(value = "/{reportNumber}/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> updateWithFile(
            @PathVariable String reportNumber,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("detectedDateTime") String detectedDateTime,
            @RequestParam("detectedLocationUnit") String detectedLocationUnit,
            @RequestParam("involvedMaterialPersonnel") String involvedMaterialPersonnel,
            @RequestParam("detailedDescription") String detailedDescription,
            HttpServletRequest request
    ) throws IOException {

        LocalDateTime detectedDateTimeParsed = null;
        String[] patterns = {"dd/MM/yyyy HH:mm", "dd/MM/yyyy"};
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                detectedDateTimeParsed = LocalDateTime.parse(detectedDateTime, formatter);
                break;
            } catch (Exception ignored) {}
        }

        if (detectedDateTimeParsed == null) {
            return ResponseEntity.badRequest().body("Formato de fecha y hora inválido. Use dd/MM/yyyy HH:mm");
        }

        if (detectedDateTimeParsed.isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha y hora detectada no puede ser mayor que la fecha y hora actual.");
        }

        Report report = service.getByReportNumber(reportNumber);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        report.setDetectedDateTime(detectedDateTimeParsed);
        report.setDetectedLocationUnit(detectedLocationUnit);
        report.setInvolvedMaterialPersonnel(involvedMaterialPersonnel);
        report.setDetailedDescription(detailedDescription);

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                return ResponseEntity.badRequest().body("El archivo debe tener un nombre válido");
            }
            String filenameLower = originalFilename.toLowerCase();
            if (!filenameLower.endsWith(".pdf") && !filenameLower.endsWith(".jpg") && !filenameLower.endsWith(".jpeg")) {
                return ResponseEntity.badRequest().body("Solo se permiten archivos PDF o JPG");
            }

            LocalDateTime now = LocalDateTime.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            String baseUploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + datePath;
            File uploadDir = new File(baseUploadDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String newFilename = UUID.randomUUID() + "_" + originalFilename;
            File destinationFile = new File(uploadDir, newFilename);
            file.transferTo(destinationFile);

            String relativePath = "uploads/" + datePath + "/" + newFilename;
            report.setEvidenceFile(relativePath);
        }

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Report updatedReport = service.updateReport(reportNumber, report, ip, userAgent);

        return ResponseEntity.ok(updatedReport);
    }

    @PatchMapping("/{reportNumber}")
    public ResponseEntity<?> patchReport(
            @PathVariable String reportNumber,
            @RequestBody ReportUpdateFieldDTO dto,
            HttpServletRequest request
    ) {
        try {
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            Report updatedReport = service.updatePartial(reportNumber, dto, ip, userAgent);

            return ResponseEntity.ok(updatedReport);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reportNumber}")
    public ResponseEntity<?> delete(@PathVariable String reportNumber) {
        service.deleteReportByNumber(reportNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/file/{year}/{month}/{day}/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename
    ) {
        try {
            Path file = Paths.get("uploads", year, month, day, filename);
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
