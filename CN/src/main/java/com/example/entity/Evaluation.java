package com.example.entity;

public class Evaluation {
    private Integer id;
    private Double rating;
    private String comment;
    private String evaluationTime;
    private Integer patientId;
    private Integer nurseId;
    private String nurseName;
    private String patientName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(String evaluationTime) {
        this.evaluationTime = evaluationTime;
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", rating='" + rating + '\'' +
                ", comment='" + comment + '\'' +
                ", evaluationTime='" + evaluationTime + '\'' +
                ", patientId=" + patientId +
                ", nurseId=" + nurseId +
                ", nurseName='" + nurseName + '\'' +
                ", patientName='" + patientName + '\'' +
                '}';
    }
}
