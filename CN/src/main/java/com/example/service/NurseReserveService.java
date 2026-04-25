package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.NurseReserve;
import com.example.entity.PageQuery;
import com.example.entity.Patient;
import com.example.exception.CustomException;
import com.example.mapper.HpMapper;
import com.example.mapper.NurseReserveMapper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class NurseReserveService {

    @Resource
    private NurseReserveMapper nurseReserveMapper;

    @Resource
    private HpMapper hpMapper;

    //新增
    public void add(NurseReserve nurseReserve){
        Integer nurseId = nurseReserve.getNurseId();
        Integer patientId = nurseReserve.getPatientId();

        // 必须处于住院中才允许预约护工（防止已出院仍可预约）
        if (patientId == null || hpMapper.countAdmittedByPatientId(patientId) <= 0) {
            throw new CustomException("当前未住院，无法预约护工");
        }

        // @NotNull 会先拦截 null，这里主要拦截“不存在”
        if (nurseReserveMapper.existsNurseById(nurseId) <= 0) {
            throw new CustomException("护工不存在(nurseId=" + nurseId + ")");
        }
        if (nurseReserveMapper.existsPatientById(patientId) <= 0) {
            throw new CustomException("患者不存在(patientId=" + patientId + ")");
        }

        // 时间格式与区间校验：若同时提供开始/结束日期，确保格式正确且结束不早于开始
        if (nurseReserve.getStartDate() != null && nurseReserve.getEndDate() != null) {
            try {
                LocalDate s = LocalDate.parse(nurseReserve.getStartDate());
                LocalDate e = LocalDate.parse(nurseReserve.getEndDate());
                if (e.isBefore(s)) {
                    throw new CustomException("结束日期不能早于开始日期");
                }
            } catch (DateTimeParseException ex) {
                throw new CustomException("预约日期格式错误，需为 YYYY-MM-DD");
            }
        }

        // 检查该患者是否已有正在服务中或待审核的护工预约，前端已有校验，但后端必须强制防护
        List<NurseReserve> existing = nurseReserveMapper.selectByPatientId(patientId);
        if (existing != null) {
            boolean blocked = existing.stream().anyMatch(r -> {
                String st = (r.getStatus() == null ? "" : r.getStatus()).trim();
                return "服务中".equals(st) || "待审核".equals(st);
            });
            if (blocked) {
                throw new CustomException("当前患者已有进行中或待审核的护工预约，无法再次预约");
            }
        }

        nurseReserve.setApplicationTime(DateUtil.now());
        nurseReserveMapper.insert(nurseReserve);
    }

    //删除
    public void deleteById(Integer id){ nurseReserveMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(NurseReserve nurseReserve){
        nurseReserveMapper.updateById(nurseReserve);
    }

    //根据id查询
    public NurseReserve selectById(Integer id){
        return nurseReserveMapper.selectById(id);
    }

    public List<NurseReserve> selectAll(PageQuery pageQuery) {
        return nurseReserveMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord());
    }

    // 通用分页接口：注意不要把 pageNum/pageSize 传入 mapper，分页由 PageHelper 控制
    public PageInfo<NurseReserve> selectPage(PageQuery pageQuery) {
        return BasePageService.pageQuery(() ->
                        // 仅传 name / patientId / nurseId 给 mapper
                        nurseReserveMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(),pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord()),
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    //返回某护工正在服务中的患者列表
    public List<Patient> selectPatientsByNurseInService(Integer nurseId){
        return nurseReserveMapper.selectPatientsByNurseInService(nurseId);
    }

    // 根据 patientId 查询该患者的所有护工预约记录
    public List<NurseReserve> selectByPatientId(Integer patientId) {
        // @NotNull 会先拦截 null，这里主要拦截“不存在”
        if (nurseReserveMapper.existsPatientById(patientId) <= 0) {
            throw new CustomException("患者不存在(patientId=" + patientId + ")");
        }
        return nurseReserveMapper.selectByPatientId(patientId);
    }
}
