package com.example.service;

import com.example.entity.Ward;
import com.example.mapper.WardMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WardService {

    @Resource
    private WardMapper wardMapper;

    //新增
    public void add(Ward ward){
        wardMapper.insert(ward);
    }

    //删除
    public void deleteById(Integer id){ wardMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(Ward ward){
        wardMapper.updateById(ward);
    }

    //根据id查询
    public Ward selectById(Integer id){
        return wardMapper.selectById(id);
    }

    //查询所有
    public List<Ward> selectAll(Ward ward){
        List<Ward> list =wardMapper.selectAll(ward);
        return list;
    }

    //分页查询
    public PageInfo<Ward> selectPage(Ward ward,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Ward> list = wardMapper.selectAll(ward);
        return PageInfo.of(list);
    }
}
