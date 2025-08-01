package springboot_app.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import springboot_app.domain.Report;
import springboot_app.dto.ReportUpdateFieldDTO;
import springboot_app.service.ReportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reportes", description = "Operaciones para gestionar informes de situación de peligro")
public class ReportController {

    @Autowired
    private ReportService service;

    @Operation(summary = "Crear reporte con archivo", description = "Crea un nuevo reporte incluyendo un archivo PDF o imagen")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte creado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Report.class)
                )),
        @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createWithFile(
            @Parameter(description = "Archivo adjunto (PDF, JPG, JPEG)", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Fecha y hora detectada (dd/MM/yyyy HH:mm)", required = true)
            @RequestParam("detectedDateTime") String detectedDateTime,

            @Parameter(description = "Unidad donde se detectó la situación", required = true)
            @RequestParam("detectedLocationUnit") String detectedLocationUnit,

            @Parameter(description = "Materiales o personal involucrado", required = true)
            @RequestParam("involvedMaterialPersonnel") String involvedMaterialPersonnel,

            @Parameter(description = "Descripción detallada de la situación", required = true)
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

    @Operation(summary = "Listar todos los reportes")
    @ApiResponse(responseCode = "200", description = "Lista de reportes")
    @GetMapping
    public List<Report> getAll() {
        return service.getAllReports();
    }

    @Operation(summary = "Obtener un reporte por su número")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @GetMapping("/{reportNumber}")
    public ResponseEntity<?> getOne(
            @Parameter(description = "Número de reporte", required = true)
            @PathVariable String reportNumber) {
        Report report = service.getByReportNumber(reportNumber);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Actualizar un reporte con nuevo archivo")
    @PutMapping(value = "/{reportNumber}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            return ResponseEntity.badRequest().body("La fecha y hora detectada no puede ser mayor que la actual.");
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

            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String baseUploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + datePath;
            File uploadDir = new File(baseUploadDir);
            if (!uploadDir.exists()) uploadDir.mkdirs();

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

    // @Operation(summary = "Actualizar parcialmente un reporte")
    @Hidden
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

    @Operation(summary = "Eliminar un reporte")
    @DeleteMapping("/{reportNumber}")
    public ResponseEntity<?> delete(
            @Parameter(description = "Número del reporte a eliminar", required = true)
            @PathVariable String reportNumber) {
        service.deleteReportByNumber(reportNumber);
        return ResponseEntity.noContent().build();
    }

    // @Operation(summary = "Descargar archivo adjunto del reporte")
    // @ApiResponses({
    //     @ApiResponse(responseCode = "200", description = "Archivo descargado correctamente"),
    //     @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    // })
    @Hidden
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
