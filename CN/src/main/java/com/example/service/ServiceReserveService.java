package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.PageQuery;
import com.example.entity.ServiceReserve;
import com.example.exception.CustomException;
import com.example.mapper.ServiceReserveMapper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceReserveService {

    @Resource
    private ServiceReserveMapper serviceReserveMapper;

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
