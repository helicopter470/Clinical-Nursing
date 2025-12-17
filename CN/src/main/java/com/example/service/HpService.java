package com.example.service;

import com.example.entity.Hp;
import com.example.entity.PageQuery;
import com.example.entity.ServiceReserve;
import com.example.mapper.HpMapper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HpService {

    @Resource
    private HpMapper hpMapper;

    //新增
    public void add(Hp hp){
        hpMapper.insert(hp);
    }

    //删除
    public void deleteById(Integer id){ hpMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(Hp hp){
        hpMapper.updateById(hp);
    }

    //根据id查询
    public Hp selectById(Integer id){
        return hpMapper.selectById(id);
    }

    public List<Hp> selectAll(PageQuery pageQuery) {
        return hpMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord());
    }

    // 通用分页接口：注意不要把 pageNum/pageSize 传入 mapper，分页由 PageHelper 控制
    public PageInfo<Hp> selectPage(PageQuery pageQuery) {
        return BasePageService.pageQuery(() ->
                        // 仅传 name / patientId / nurseId 给 mapper
                        hpMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord()),
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    // 根据 patientId 查询该患者的所有住院记录
    public List<Hp> selectByPatientId(Integer patientId) {
        return hpMapper.selectByPatientId(patientId);
    }
}
