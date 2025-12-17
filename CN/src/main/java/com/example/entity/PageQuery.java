package com.example.entity;

public class PageQuery {
    private String name;
    private String nurseName;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Integer patientId;
    private Integer nurseId;
    private String record;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    @Override
    public String toString() {
        return "PageQuery{" +
                "name='" + name + '\'' +
                ", nurseName='" + nurseName + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", patientId=" + patientId +
                ", nurseId=" + nurseId +
                ", record='" + record + '\'' +
                '}';
    }
}
