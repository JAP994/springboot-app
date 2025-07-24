package springboot_app.controllers;

import springboot_app.domain.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ReportController {

    private List<Report> reports = new ArrayList<>(Arrays.asList(
        new Report(0, "DIRTIC202501", null, null, "DIRTIC", "SILLAS", "APP/TEST", "SILLAS OXIDADAS", "10.238.0.0", "JASANCHEZP", null),
        new Report(1, "DIRTIC202502", null, null, "DIRTIC", "SILLAS", "APP/TEST", "SILLAS OXIDADAS", "10.238.0.0", "JASANCHEZP", null),
        new Report(2, "DIRTIC202503", null, null, "DIRSEC", "LAPTOPS", "APP/TEST", "LAPTOPS OXIDADAS", "10.238.0.0", "JASANCHEZP", null),
        new Report(3, "DIRTIC202504", null, null, "DIRTIC", "SILLAS", "APP/TEST", "SILLAS OXIDADAS", "10.238.0.0", "JASANCHEZP", null)
    ));

    @GetMapping("/reports")    
    public List<Report> getReport(){
        return reports;
    }

    @GetMapping("/reports/{reportNumber}")    
    public Report getReport(@PathVariable String reportNumber){
        for(Report report : reports){
            if (report.getNumeroInforme().equalsIgnoreCase(reportNumber)) {
                return report;
            }
        }
        return null;
    }

    @PostMapping("/reports")
    public Report postReport(@RequestBody Report report){
        reports.add(report);
        return report;
    }

    @PutMapping("/reports")
    public Report putReport(@RequestBody Report report){
        System.out.println(report);
        for(Report r: reports){
            if (r.getNumeroInforme().equalsIgnoreCase(report.getNumeroInforme())) {
                r.setFechaHoraDetectado(report.getFechaHoraDetectado());
                r.setRepartoLugarDetectado(report.getRepartoLugarDetectado());
                r.setMaterialPersonalInvolucrado(report.getMaterialPersonalInvolucrado());
                r.setArchivoEvidencia(report.getArchivoEvidencia());
                r.setDescripcionDetallada(report.getDescripcionDetallada());
                
                return r;
            }
        }
        return null;
    }

    @DeleteMapping("/reports/{reportNumber}")
    public Report deleteReport(@PathVariable String reportNumber){
        for(Report report: reports){
            if (report.getNumeroInforme().equalsIgnoreCase(reportNumber)) {
                reports.remove(report);
                
                return report;
            }

        }
        return null;
    }

    @PatchMapping("/reports")
    public Report patchReport(@RequestBody Report report){
        
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

                return r;
            }
        }
        return null;
    }

}
