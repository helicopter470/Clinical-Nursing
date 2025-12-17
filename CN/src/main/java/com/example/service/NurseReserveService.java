package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.NurseReserve;
import com.example.entity.PageQuery;
import com.example.entity.Patient;
import com.example.entity.ServiceReserve;
import com.example.mapper.NurseReserveMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NurseReserveService {

    @Resource
    private NurseReserveMapper nurseReserveMapper;

    //新增
    public void add(NurseReserve nurseReserve){
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
        return nurseReserveMapper.selectByPatientId(patientId);
    }
}
