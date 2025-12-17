package com.example.entity;

public class NurseReserve {
    private Integer id;
    private String startDate;
    private String endDate;
    private String status;
    private String applicationTime;
    private String reserveOption;
    private Integer nurseId;
    private Integer patientId;
    private String nurseName;
    private String patientName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(String applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getReserveOption() {
        return reserveOption;
    }

    public void setReserveOption(String reserveOption) {
        this.reserveOption = reserveOption;
    }

    public Integer getNurseId() {
        return nurseId;
    }

    public void setNurseId(Integer nurseId) {
        this.nurseId = nurseId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "NurseReserve{" +
                "id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status='" + status + '\'' +
                ", applicationTime='" + applicationTime + '\'' +
                ", reserveOption='" + reserveOption + '\'' +
                ", nurseId=" + nurseId +
                ", patientId=" + patientId +
                ", nurseName='" + nurseName + '\'' +
                ", patientName='" + patientName + '\'' +
                '}';
    }
}
