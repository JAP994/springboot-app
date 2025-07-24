package springboot_app.domain;

import java.time.LocalDateTime;

public class Report {
    private int id;

    private String numeroInforme;

    private LocalDateTime fechaHoraInforme;

    private LocalDateTime fechaHoraDetectado;

    private String repartoLugarDetectado;

    private String materialPersonalInvolucrado;

    private String archivoEvidencia; // Se guardará la ruta del archivo

    private String descripcionDetallada;

    // Campos de auditoría
    private String ipRegistro;

    private String userAgent;

    private LocalDateTime fechaCreacion;

    // Metodos
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroInforme() {
        return numeroInforme;
    }

    public void setNumeroInforme(String numeroInforme) {
        this.numeroInforme = numeroInforme;
    }

    public LocalDateTime getFechaHoraInforme() {
        return fechaHoraInforme;
    }

    public void setFechaHoraInforme(LocalDateTime fechaHoraInforme) {
        this.fechaHoraInforme = fechaHoraInforme;
    }

    public LocalDateTime getFechaHoraDetectado() {
        return fechaHoraDetectado;
    }

    public void setFechaHoraDetectado(LocalDateTime fechaHoraDetectado) {
        this.fechaHoraDetectado = fechaHoraDetectado;
    }

    public String getRepartoLugarDetectado() {
        return repartoLugarDetectado;
    }

    public void setRepartoLugarDetectado(String repartoLugarDetectado) {
        this.repartoLugarDetectado = repartoLugarDetectado;
    }

    public String getMaterialPersonalInvolucrado() {
        return materialPersonalInvolucrado;
    }

    public void setMaterialPersonalInvolucrado(String materialPersonalInvolucrado) {
        this.materialPersonalInvolucrado = materialPersonalInvolucrado;
    }

    public String getArchivoEvidencia() {
        return archivoEvidencia;
    }

    public void setArchivoEvidencia(String archivoEvidencia) {
        this.archivoEvidencia = archivoEvidencia;
    }

    public String getDescripcionDetallada() {
        return descripcionDetallada;
    }

    public void setDescripcionDetallada(String descripcionDetallada) {
        this.descripcionDetallada = descripcionDetallada;
    }

    public String getIpRegistro() {
        return ipRegistro;
    }

    public void setIpRegistro(String ipRegistro) {
        this.ipRegistro = ipRegistro;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    // Constructor
    public Report(int id, String numeroInforme, LocalDateTime fechaHoraInforme, LocalDateTime fechaHoraDetectado,
            String repartoLugarDetectado, String materialPersonalInvolucrado, String archivoEvidencia,
            String descripcionDetallada, String ipRegistro, String userAgent, LocalDateTime fechaCreacion) {
        this.id = id;
        this.numeroInforme = numeroInforme;
        this.fechaHoraInforme = fechaHoraInforme;
        this.fechaHoraDetectado = fechaHoraDetectado;
        this.repartoLugarDetectado = repartoLugarDetectado;
        this.materialPersonalInvolucrado = materialPersonalInvolucrado;
        this.archivoEvidencia = archivoEvidencia;
        this.descripcionDetallada = descripcionDetallada;
        this.ipRegistro = ipRegistro;
        this.userAgent = userAgent;
        this.fechaCreacion = fechaCreacion;
    }
}
