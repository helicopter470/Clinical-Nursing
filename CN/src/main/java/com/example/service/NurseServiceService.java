package com.example.service;

import com.example.mapper.NurseServiceMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.example.entity.NurseService;

import java.util.List;

@Service
public class NurseServiceService {

    @Resource
    private NurseServiceMapper nurseServiceMapper;

    //新增
    public void add(NurseService nurseService){
        nurseServiceMapper.insert(nurseService);
    }

    //删除
    public void deleteById(Integer id){ nurseServiceMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(NurseService nurseService){
        nurseServiceMapper.updateById(nurseService);
    }

    //根据id查询
    public NurseService selectById(Integer id){
        return nurseServiceMapper.selectById(id);
    }

    //查询所有
    public List<NurseService> selectAll(NurseService nurseService){
        List<NurseService> list =nurseServiceMapper.selectAll(nurseService);
        return list;
    }

    //分页查询
    public PageInfo<NurseService> selectPage(NurseService nurseService,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<NurseService> list = nurseServiceMapper.selectAll(nurseService);
        return PageInfo.of(list);
    }
}
