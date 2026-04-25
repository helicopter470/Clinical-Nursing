package com.example.service;

import com.example.entity.DailyMonitor;
import com.example.entity.PageQuery;
import com.example.mapper.DailyMonitorMapper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyMonitorService {

    @Resource
    private DailyMonitorMapper dailyMonitorMapper;

    //新增
    public void add(DailyMonitor dailyMonitor){
        dailyMonitorMapper.insert(dailyMonitor);
    }

    //删除
    public void deleteById(Integer id){ dailyMonitorMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(DailyMonitor dailyMonitor){
        dailyMonitorMapper.updateById(dailyMonitor);
    }

    //根据id查询
    public DailyMonitor selectById(Integer id){
        return dailyMonitorMapper.selectById(id);
    }

    public List<DailyMonitor> selectAll(PageQuery pageQuery) {
        return dailyMonitorMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord());
    }

    // 通用分页接口：注意不要把 pageNum/pageSize 传入 mapper，分页由 PageHelper 控制
    public PageInfo<DailyMonitor> selectPage(PageQuery pageQuery) {
        return BasePageService.pageQuery(() ->
                        // 仅传 name / patientId / nurseId 给 mapper
                        dailyMonitorMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord()),
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }

}
