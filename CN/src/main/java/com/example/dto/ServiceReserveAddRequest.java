package com.example.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 护理服务预约新增请求：由后端根据患者当前“服务中”的护工预约自动补全 nurseId。
 */
public class ServiceReserveAddRequest {

    @NotNull(message = "serviceId不能为空")
    private Integer serviceId;

    @NotNull(message = "patientId不能为空")
    private Integer patientId;

    // 预约时间（YYYY-MM-DD）
    private String startDate;
    private String endDate;

    // 价格（前端计算并提交，后端可再做二次校验/重算）
    private String price;

    // 状态（通常为 待审核）
    private String status;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
