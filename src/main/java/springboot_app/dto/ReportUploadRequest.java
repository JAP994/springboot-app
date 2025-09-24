package springboot_app.dto;

import org.springframework.web.multipart.MultipartFile;

public class ReportUploadRequest {

    private MultipartFile file;
    private String reportNumber;
    private String detectedDateTime;
    private String detectedLocationUnit;
    private String involvedMaterialPersonnel;
    private String detailedDescription;

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }

    public String getDetectedDateTime() { return detectedDateTime; }
    public void setDetectedDateTime(String detectedDateTime) { this.detectedDateTime = detectedDateTime; }

    public String getDetectedLocationUnit() { return detectedLocationUnit; }
    public void setDetectedLocationUnit(String detectedLocationUnit) { this.detectedLocationUnit = detectedLocationUnit; }

    public String getInvolvedMaterialPersonnel() { return involvedMaterialPersonnel; }
    public void setInvolvedMaterialPersonnel(String involvedMaterialPersonnel) { this.involvedMaterialPersonnel = involvedMaterialPersonnel; }

    public String getDetailedDescription() { return detailedDescription; }
    public void setDetailedDescription(String detailedDescription) { this.detailedDescription = detailedDescription; }
}
