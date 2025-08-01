package springboot_app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String reportNumber;

    @NotNull
    private LocalDateTime reportDateTime;

    @NotNull
    private LocalDateTime detectedDateTime;

    @NotBlank
    @Size(max = 100)
    private String detectedLocationUnit;

    @NotBlank
    @Size(max = 200)
    private String involvedMaterialPersonnel;

    @NotBlank
    private String evidenceFile;

    @NotBlank
    @Size(max = 400)
    private String detailedDescription;

    // Auditoría creación
    private String registrationIp;
    private String userAgent;
    private LocalDateTime creationDate;

    // Auditoría modificación
    private String lastModifiedIp;
    private String lastModifiedUserAgent;
    private LocalDateTime lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
        this.reportDateTime = LocalDateTime.now();
    }

    // Getters y setters...

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }

    public LocalDateTime getReportDateTime() { return reportDateTime; }
    public void setReportDateTime(LocalDateTime reportDateTime) { this.reportDateTime = reportDateTime; }

    public LocalDateTime getDetectedDateTime() { return detectedDateTime; }
    public void setDetectedDateTime(LocalDateTime detectedDateTime) { this.detectedDateTime = detectedDateTime; }

    public String getDetectedLocationUnit() { return detectedLocationUnit; }
    public void setDetectedLocationUnit(String detectedLocationUnit) { this.detectedLocationUnit = detectedLocationUnit; }

    public String getInvolvedMaterialPersonnel() { return involvedMaterialPersonnel; }
    public void setInvolvedMaterialPersonnel(String involvedMaterialPersonnel) { this.involvedMaterialPersonnel = involvedMaterialPersonnel; }

    public String getEvidenceFile() { return evidenceFile; }
    public void setEvidenceFile(String evidenceFile) { this.evidenceFile = evidenceFile; }

    public String getDetailedDescription() { return detailedDescription; }
    public void setDetailedDescription(String detailedDescription) { this.detailedDescription = detailedDescription; }

    public String getRegistrationIp() { return registrationIp; }
    public void setRegistrationIp(String registrationIp) { this.registrationIp = registrationIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public String getLastModifiedIp() { return lastModifiedIp; }
    public void setLastModifiedIp(String lastModifiedIp) { this.lastModifiedIp = lastModifiedIp; }

    public String getLastModifiedUserAgent() { return lastModifiedUserAgent; }
    public void setLastModifiedUserAgent(String lastModifiedUserAgent) { this.lastModifiedUserAgent = lastModifiedUserAgent; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public Report() {}
}
