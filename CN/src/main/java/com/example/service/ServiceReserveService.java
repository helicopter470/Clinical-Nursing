package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.dto.ServiceReserveAddRequest;
import com.example.entity.PageQuery;
import com.example.entity.NurseReserve;
import com.example.entity.ServiceReserve;
import com.example.exception.CustomException;
import com.example.mapper.HpMapper;
import com.example.mapper.NurseReserveMapper;
import com.example.mapper.ServiceReserveMapper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class ServiceReserveService {

    @Resource
    private ServiceReserveMapper serviceReserveMapper;
    @Resource
    private NurseReserveMapper nurseReserveMapper;
    @Resource
    private HpMapper hpMapper;

    /**
     * DTO 入口：由后端根据患者当前“服务中”的护工预约补齐 nurseId。
     */
    public void addFromRequest(ServiceReserveAddRequest request) {
        if (request == null) {
            throw new CustomException("请求不能为空");
        }
        Integer patientId = request.getPatientId();
        Integer serviceId = request.getServiceId();

        // 必须处于住院中才允许预约护理服务
        if (patientId == null || hpMapper.countAdmittedByPatientId(patientId) <= 0) {
            throw new CustomException("当前未住院，无法预约护理服务");
        }

        // 找到该患者当前“服务中”的护工预约
        List<NurseReserve> nurseReserves = nurseReserveMapper.selectByPatientId(patientId);
        NurseReserve active = nurseReserves == null ? null : nurseReserves.stream().filter(r -> {
            String st = (r.getStatus() == null ? "" : r.getStatus()).trim();
            return "服务中".equals(st);
        }).findFirst().orElse(null);
        if (active == null || active.getNurseId() == null) {
            throw new CustomException("请先预约护工并处于 \"服务中\" 后才能预约护理服务");
        }

        ServiceReserve entity = new ServiceReserve();
        entity.setPatientId(patientId);
        entity.setServiceId(serviceId);
        entity.setNurseId(active.getNurseId());
        entity.setStatus(request.getStatus() == null ? "待审核" : request.getStatus());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setPrice(request.getPrice());

        // 复用原 add 逻辑（包含日期校验、重复服务校验、区间校验等）
        this.add(entity);
    }

    //新增
    public void add(ServiceReserve serviceReserve){
        Integer serviceId = serviceReserve.getServiceId();
        Integer nurseId = serviceReserve.getNurseId();
        Integer patientId = serviceReserve.getPatientId();
        // @NotNull 会先拦截 null，这里主要拦截“不存在”
        if (serviceReserveMapper.existsServiceById(serviceId) <= 0) {
            throw new CustomException("服务不存在(serviceId=" + serviceId + ")");
        }
        if (serviceReserveMapper.existsNurseById(nurseId) <= 0) {
            throw new CustomException("护工不存在(nurseId=" + nurseId + ")");
        }
        if (serviceReserveMapper.existsPatientById(patientId) <= 0) {
            throw new CustomException("患者不存在(patientId=" + patientId + ")");
        }

        // 校验开始/结束日期格式及顺序（若提供）
        if (serviceReserve.getStartDate() != null && serviceReserve.getEndDate() != null) {
            try {
                LocalDate s = LocalDate.parse(serviceReserve.getStartDate());
                LocalDate e = LocalDate.parse(serviceReserve.getEndDate());
                if (e.isBefore(s)) {
                    throw new CustomException("服务结束日期不能早于开始日期");
                }
            } catch (DateTimeParseException ex) {
                throw new CustomException("预约日期格式错误，需为 YYYY-MM-DD");
            }
        }

        // 检查患者是否已有正在服务中的护理服务，避免重复预约
        List<ServiceReserve> svcExisting = serviceReserveMapper.selectByPatientId(patientId);
        if (svcExisting != null) {
            boolean hasActive = svcExisting.stream().anyMatch(s -> {
                String st = (s.getStatus() == null ? "" : s.getStatus()).trim();
                return "服务中".equals(st);
            });
            if (hasActive) {
                throw new CustomException("该患者已有正在服务中的护理服务，无法重复预约");
            }
        }

        // 必须存在一个与该患者关联且状态为“服务中”的护工预约，并且服务区间需在护工预约区间内（如护工有结束日期才校验）
        List<NurseReserve> nurseReserves = nurseReserveMapper.selectByPatientId(patientId);
        if (nurseReserves == null) {
            throw new CustomException("请先预约护工并处于 \"服务中\" 后才能预约护理服务");
        }
        NurseReserve active = nurseReserves.stream().filter(r -> {
            String st = (r.getStatus() == null ? "" : r.getStatus()).trim();
            return "服务中".equals(st);
        }).findFirst().orElse(null);
        if (active == null) {
            throw new CustomException("请先预约护工并处于 \"服务中\" 后才能预约护理服务");
        }

        // 护工必须与用户提交的一致
        if (active.getNurseId() != null && !active.getNurseId().equals(nurseId)) {
            throw new CustomException("所选护工与当前正在服务的护工不一致");
        }

        // 若护工预约有开始/结束日期，则校验服务时段在该区间内（按日期比较，忽略时分）
        String nurseStartRaw = active.getStartDate();
        String nurseEndRaw = active.getEndDate();
        if (nurseStartRaw != null && serviceReserve.getStartDate() != null) {
            try {
                LocalDate ns = LocalDate.parse(nurseStartRaw);
                LocalDate ss = LocalDate.parse(serviceReserve.getStartDate());
                if (ss.isBefore(ns)) {
                    throw new CustomException("服务开始日期不能早于护工预约开始日期");
                }
            } catch (DateTimeParseException ex) {
                throw new CustomException("护工预约开始日期格式错误，需为 YYYY-MM-DD");
            }
        }
        if (nurseEndRaw != null && serviceReserve.getEndDate() != null) {
            try {
                LocalDate ne = LocalDate.parse(nurseEndRaw);
                LocalDate se = LocalDate.parse(serviceReserve.getEndDate());
                if (se.isAfter(ne)) {
                    throw new CustomException("服务结束日期不能晚于护工预约结束日期");
                }
            } catch (DateTimeParseException ex) {
                throw new CustomException("护工预约结束日期格式错误，需为 YYYY-MM-DD");
            }
        }

        // 必须处于住院中才允许预约护理服务
        if (patientId == null || hpMapper.countAdmittedByPatientId(patientId) <= 0) {
            throw new CustomException("当前未住院，无法预约护理服务");
        }

        serviceReserve.setApplicationTime(DateUtil.now());
        serviceReserveMapper.insert(serviceReserve);
    }

    //删除
    public void deleteById(Integer id){ serviceReserveMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(ServiceReserve serviceReserve){
        serviceReserveMapper.updateById(serviceReserve);
    }

    //根据id查询
    public ServiceReserve selectById(Integer id){
        return serviceReserveMapper.selectById(id);
    }

    public List<ServiceReserve> selectAll(PageQuery pageQuery) {
        return serviceReserveMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord());
    }

    // 通用分页接口，分页由 PageHelper 控制
    public PageInfo<ServiceReserve> selectPage(PageQuery pageQuery) {
        return BasePageService.pageQuery(() ->
                        // 仅传 name / patientId / nurseId 给 mapper
                        serviceReserveMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(),pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord()),
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    public List<ServiceReserve> selectByPatientId(Integer patientId) {
        return serviceReserveMapper.selectByPatientId(patientId);
    }
}
