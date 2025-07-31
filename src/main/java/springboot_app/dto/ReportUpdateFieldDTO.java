package springboot_app.dto;

import java.time.LocalDateTime;

public class ReportUpdateFieldDTO {

    private LocalDateTime detectedDateTime;
    private String detectedLocationUnit;
    private String involvedMaterialPersonnel;
    private String detailedDescription;

    public LocalDateTime getDetectedDateTime() {
        return detectedDateTime;
    }

    public void setDetectedDateTime(LocalDateTime detectedDateTime) {
        this.detectedDateTime = detectedDateTime;
    }

    public String getDetectedLocationUnit() {
        return detectedLocationUnit;
    }

    public void setDetectedLocationUnit(String detectedLocationUnit) {
        this.detectedLocationUnit = detectedLocationUnit;
    }

    public String getInvolvedMaterialPersonnel() {
        return involvedMaterialPersonnel;
    }

    public void setInvolvedMaterialPersonnel(String involvedMaterialPersonnel) {
        this.involvedMaterialPersonnel = involvedMaterialPersonnel;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }
}
