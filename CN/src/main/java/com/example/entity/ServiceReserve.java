package com.example.entity;

public class ServiceReserve {
    private Integer id;
    private String startDate;
    private String endDate;
    private String price;
    private String applicationTime;
    private String status;
    private String reserveOption;
    private String review;
    private Integer serviceId;
    private Integer nurseId;
    private Integer patientId;
    private String serverName;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(String applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReserveOption() {
        return reserveOption;
    }

    public void setReserveOption(String reserveOption) {
        this.reserveOption = reserveOption;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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
        return "ServerReserve{" +
                "id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", price='" + price + '\'' +
                ", applicationTime='" + applicationTime + '\'' +
                ", status='" + status + '\'' +
                ", reserveOption='" + reserveOption + '\'' +
                ", review='" + review + '\'' +
                ", serviceId=" + serviceId +
                ", nurseId=" + nurseId +
                ", patientId=" + patientId +
                ", serverName='" + serverName + '\'' +
                ", nurseName='" + nurseName + '\'' +
                ", patientName='" + patientName + '\'' +
                '}';
    }
}
