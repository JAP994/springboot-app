package springboot_app.controllers;

import springboot_app.domain.Report;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/reports")
public class ReportController {

    private List<Report> reports = new ArrayList<>(Arrays.asList(
        new Report(0, "DIRTIC202501", null, null, "DIRTIC", "SILLAS", "APP/TEST", "SILLAS OXIDADAS", "10.238.0.0", "JASANCHEZP", null),
        new Report(1, "DIRTIC202502", null, null, "DIRTIC", "SILLAS", "APP/TEST", "SILLAS OXIDADAS", "10.238.0.0", "JASANCHEZP", null),
        new Report(2, "DIRTIC202503", null, null, "DIRSEC", "LAPTOPS", "APP/TEST", "LAPTOPS OXIDADAS", "10.238.0.0", "JASANCHEZP", null),
        new Report(3, "DIRTIC202504", null, null, "DIRTIC", "SILLAS", "APP/TEST", "SILLAS OXIDADAS", "10.238.0.0", "JASANCHEZP", null)
    ));

    // @RequestMapping(method = RequestMethod.GET)
    @GetMapping    
    public ResponseEntity<List<Report>> getReport(){
        return ResponseEntity.ok(reports);
    }

    // @RequestMapping(value = "/{reportNumber}", method = RequestMethod.GET)
    @GetMapping("/{reportNumber}")    
    public ResponseEntity<?> getReport(@PathVariable String reportNumber){
        for(Report report : reports){
            if (report.getNumeroInforme().equalsIgnoreCase(reportNumber)) {
                return ResponseEntity.ok(report);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Informe no encontrado con numero de informe: " + reportNumber);
    }

    // @RequestMapping(method = RequestMethod.POST)
    @PostMapping
    public ResponseEntity<?> postReport(@RequestBody Report report){
        reports.add(report);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{reportNumber}").buildAndExpand(report.getNumeroInforme()).toUri();
        
        return ResponseEntity.created(location).body(report);
                
    }

    // @RequestMapping(method = RequestMethod.PUT)
    @PutMapping
    public ResponseEntity<?> putReport(@RequestBody Report report){
        System.out.println(report);
        for(Report r: reports){
            if (r.getNumeroInforme().equalsIgnoreCase(report.getNumeroInforme())) {
                r.setFechaHoraDetectado(report.getFechaHoraDetectado());
                r.setRepartoLugarDetectado(report.getRepartoLugarDetectado());
                r.setMaterialPersonalInvolucrado(report.getMaterialPersonalInvolucrado());
                r.setArchivoEvidencia(report.getArchivoEvidencia());
                r.setDescripcionDetallada(report.getDescripcionDetallada());
                
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    // @RequestMapping(value = "/{reportNumber}", method = RequestMethod.DELETE)
    @DeleteMapping("/{reportNumber}")
    public ResponseEntity<?> deleteReport(@PathVariable String reportNumber){
        for(Report report: reports){
            if (report.getNumeroInforme().equalsIgnoreCase(reportNumber)) {
                reports.remove(report);
                
                return ResponseEntity.noContent().build();
            }

        }
        return ResponseEntity.notFound().build();
    }

    // @RequestMapping(method = RequestMethod.PATCH)
    @PatchMapping
    public ResponseEntity<?> patchReport(@RequestBody Report report){
        
        for(Report r: reports){
            if (r.getNumeroInforme().equalsIgnoreCase(report.getNumeroInforme())) {
                
                if (report.getRepartoLugarDetectado() != null) {
                    r.setRepartoLugarDetectado(report.getRepartoLugarDetectado());
                }
                if (report.getMaterialPersonalInvolucrado() != null) {
                    r.setMaterialPersonalInvolucrado(report.getMaterialPersonalInvolucrado());
                }
                if (report.getArchivoEvidencia() != null) {
                    r.setArchivoEvidencia(report.getArchivoEvidencia());
                }
                if (report.getDescripcionDetallada() != null) {
                    r.setDescripcionDetallada(report.getDescripcionDetallada());
                }

                return ResponseEntity.ok("Cliente modificado exitosamente: " + report.getNumeroInforme());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Informe no encontrado: " + report.getNumeroInforme());
    }
}
