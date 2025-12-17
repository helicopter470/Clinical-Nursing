package com.example.entity;

public class Hp {
    private Integer id;
    private String bedNum;
    private String illness;
    private String illnessDesc;
    private String treatment;
    private String status;
    private String admitDate;
    private String dischargeDate;
    private String phoneId;
    private String emergencyContact;
    private String emergencyPhone;
    private Integer patientId;
    private Integer wardId;
    private String patientName;
    private String wardName;
    private String patientPhone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBedNum() {
        return bedNum;
    }

    public void setBedNum(String bedNum) {
        this.bedNum = bedNum;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getIllnessDesc() {
        return illnessDesc;
    }

    public void setIllnessDesc(String illnessDesc) {
        this.illnessDesc = illnessDesc;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(String admitDate) {
        this.admitDate = admitDate;
    }

    public String getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getWardId() {
        return wardId;
    }

    public void setWardId(Integer wardId) {
        this.wardId = wardId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    @Override
    public String toString() {
        return "Hp{" +
                "id=" + id +
                ", bedNum='" + bedNum + '\'' +
                ", illness='" + illness + '\'' +
                ", illnessDesc='" + illnessDesc + '\'' +
                ", treatment='" + treatment + '\'' +
                ", status='" + status + '\'' +
                ", admitDate='" + admitDate + '\'' +
                ", dischargeDate='" + dischargeDate + '\'' +
                ", phoneId='" + phoneId + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", emergencyPhone='" + emergencyPhone + '\'' +
                ", patientId=" + patientId +
                ", wardId=" + wardId +
                ", patientName='" + patientName + '\'' +
                ", wardName='" + wardName + '\'' +
                ", patientPhone='" + patientPhone + '\'' +
                '}';
    }
}
