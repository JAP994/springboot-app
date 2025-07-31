package springboot_app.controllers;

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
            @RequestParam("reportNumber") String reportNumber,
            @RequestParam("detectedDateTime") String detectedDateTime,
            @RequestParam("detectedLocationUnit") String detectedLocationUnit,
            @RequestParam("involvedMaterialPersonnel") String involvedMaterialPersonnel,
            @RequestParam("detailedDescription") String detailedDescription,
            HttpServletRequest request
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo obligatorio");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            return ResponseEntity.badRequest().body("El archivo debe tener un nombre válido");
        }

        String filenameLower = originalFilename.toLowerCase();

        if (!filenameLower.endsWith(".pdf") && !filenameLower.endsWith(".jpg")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF o JPG");
        }

        LocalDateTime detectedDateTimeParsed = null;
        String[] patterns = {
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                detectedDateTimeParsed = LocalDateTime.parse(detectedDateTime, formatter);
                break;
            } catch (Exception ignored) {}
        }

        if (detectedDateTimeParsed == null) {
            return ResponseEntity.badRequest().body("Formato de fecha y hora inválido. Use formato ISO 8601.");
        }

        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String baseUploadDir =  System.getProperty("user.dir") + File.separator + "uploads" + File.separator + datePath;
        File uploadDir = new File(baseUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String newFilename = UUID.randomUUID() + "_" + originalFilename;
        File destinationFile = new File(uploadDir, newFilename);
        file.transferTo(destinationFile);

        // Ruta relativa para guardar en la base de datos o devolver al cliente
        String relativePath = "uploads/" + datePath + "/" + newFilename;


        Report report = new Report();
        report.setReportNumber(reportNumber);
        report.setDetectedDateTime(detectedDateTimeParsed);
        report.setDetectedLocationUnit(detectedLocationUnit);
        report.setInvolvedMaterialPersonnel(involvedMaterialPersonnel);
        report.setDetailedDescription(detailedDescription);
        report.setEvidenceFile(relativePath);

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        return ResponseEntity.ok(service.saveReport(report, ip, userAgent));
    }

    @GetMapping
    public List<Report> getAll() {
        return service.getAllReports();
    }

    @GetMapping("/{reportNumber}")
    public Report getOne(@PathVariable String reportNumber) {
        return service.getByReportNumber(reportNumber);
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

        // Valida y parsea fecha
        LocalDateTime detectedDateTimeParsed = null;
        String[] patterns = {
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                detectedDateTimeParsed = LocalDateTime.parse(detectedDateTime, formatter);
                break;
            } catch (Exception ignored) {}
        }

        if (detectedDateTimeParsed == null) {
            return ResponseEntity.badRequest().body("Formato de fecha y hora inválido. Use formato ISO 8601.");
        }

        Report report = service.getByReportNumber(reportNumber);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        report.setDetectedDateTime(detectedDateTimeParsed);
        report.setDetectedLocationUnit(detectedLocationUnit);
        report.setInvolvedMaterialPersonnel(involvedMaterialPersonnel);
        report.setDetailedDescription(detailedDescription);

        // Si se envió archivo, guardarlo y actualizar ruta
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                return ResponseEntity.badRequest().body("El archivo debe tener un nombre válido");
            }
            String filenameLower = originalFilename.toLowerCase();
            if (!filenameLower.endsWith(".pdf") && !filenameLower.endsWith(".jpg")) {
                return ResponseEntity.badRequest().body("Solo se permiten archivos PDF o JPG");
            }

            LocalDateTime now = LocalDateTime.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            String baseUploadDir =  System.getProperty("user.dir") + File.separator + "uploads" + File.separator + datePath;
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
    public Report patch(@PathVariable String reportNumber, @RequestBody ReportUpdateFieldDTO dto) {
        return service.updatePartial(reportNumber, dto);
    }

    @DeleteMapping("/{reportNumber}")
    public ResponseEntity<?> delete(@PathVariable String reportNumber) {
        service.deleteReport(reportNumber);
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
