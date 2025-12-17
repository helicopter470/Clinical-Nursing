package com.example.entity;

public class DailyMonitor {
    private Integer id;
    private String blood;
    private String temp;
    private String pulse;
    private String breathe;
    private String mental;
    private String dietary;
    private String record;
    private String remark;
    private Integer patientId;
    private Integer nurseId;
    private Integer wardId;
    private Integer bedId;
    private String nurseName;
    private String patientName;
    private String wardName;
    private String bedNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getBreathe() {
        return breathe;
    }

    public void setBreathe(String breathe) {
        this.breathe = breathe;
    }

    public String getMental() {
        return mental;
    }

    public void setMental(String mental) {
        this.mental = mental;
    }

    public String getDietary() {
        return dietary;
    }

    public void setDietary(String dietary) {
        this.dietary = dietary;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getNurseId() {
        return nurseId;
    }

    public void setNurseId(Integer nurseId) {
        this.nurseId = nurseId;
    }

    public Integer getWardId() {
        return wardId;
    }

    public void setWardId(Integer wardId) {
        this.wardId = wardId;
    }

    public Integer getBedId() {
        return bedId;
    }

    public void setBedId(Integer bedId) {
        this.bedId = bedId;
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

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getBedName() {
        return bedNum;
    }

    public void setBedName(String bedNum) {
        this.bedNum = bedNum;
    }

    @Override
    public String toString() {
        return "DailyMonitor{" +
                "id=" + id +
                ", blood='" + blood + '\'' +
                ", temp='" + temp + '\'' +
                ", pulse='" + pulse + '\'' +
                ", breathe='" + breathe + '\'' +
                ", mental='" + mental + '\'' +
                ", dietary='" + dietary + '\'' +
                ", record='" + record + '\'' +
                ", remark='" + remark + '\'' +
                ", patientId=" + patientId +
                ", nurseId=" + nurseId +
                ", wardId=" + wardId +
                ", bedId=" + bedId +
                ", nurseName='" + nurseName + '\'' +
                ", patientName='" + patientName + '\'' +
                ", wardName='" + wardName + '\'' +
                ", bedNum='" + bedNum + '\'' +
                '}';
    }
}
