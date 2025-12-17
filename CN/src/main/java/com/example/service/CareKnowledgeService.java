package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.CareKnowledge;
import com.example.mapper.CareKnowledgeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareKnowledgeService {

    @Resource
    private CareKnowledgeMapper careKnowledgeMapper;

    //新增
    public void add(CareKnowledge careKnowledge){
        careKnowledge.setTime(DateUtil.now());
        careKnowledgeMapper.insert(careKnowledge);
    }

    //删除
    public void deleteById(Integer id){ careKnowledgeMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(CareKnowledge careKnowledge){
        careKnowledgeMapper.updateById(careKnowledge);
    }

    //根据id查询
    public CareKnowledge selectById(Integer id){
        return careKnowledgeMapper.selectById(id);
    }

    //查询所有
    public List<CareKnowledge> selectAll(CareKnowledge careKnowledge){
        List<CareKnowledge> list =careKnowledgeMapper.selectAll(careKnowledge);
        return list;
    }

    //分页查询
    public PageInfo<CareKnowledge> selectPage(CareKnowledge careKnowledge,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<CareKnowledge> list = careKnowledgeMapper.selectAll(careKnowledge);
        return PageInfo.of(list);
    }

    //阅读量更新
    public boolean incrementClick(Integer id){
        return careKnowledgeMapper.incrementClick(id) > 0;
    }
}
