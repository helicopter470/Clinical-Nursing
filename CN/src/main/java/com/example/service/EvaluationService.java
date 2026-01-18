package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Evaluation;
import com.example.entity.PageQuery;
import com.example.entity.ServiceReserve;
import com.example.exception.CustomException;
import com.example.mapper.EvaluationMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationService {

    @Resource
    private EvaluationMapper evaluationMapper;

    //新增
    public void add(Evaluation evaluation){
        Integer nurseId = evaluation.getNurseId();
        Integer patientId = evaluation.getPatientId();

        // @NotNull 会先拦截 null，这里主要拦截“不存在”
        if (evaluationMapper.existsNurseById(nurseId) <= 0) {
            throw new CustomException("护工不存在(nurseId=" + nurseId + ")");
        }
        if (evaluationMapper.existsPatientById(patientId) <= 0) {
            throw new CustomException("患者不存在(patientId=" + patientId + ")");
        }
        evaluation.setEvaluationTime(DateUtil.now());
        evaluationMapper.insert(evaluation);
    }

    //删除
    public void deleteById(Integer id){ evaluationMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(Evaluation evaluation){
        evaluationMapper.updateById(evaluation);
    }

    //根据id查询
    public Evaluation selectById(Integer id){
        return evaluationMapper.selectById(id);
    }

    public List<Evaluation> selectAll(PageQuery pageQuery) {
        return evaluationMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord());
    }

    // 通用分页接口：注意不要把 pageNum/pageSize 传入 mapper，分页由 PageHelper 控制
    public PageInfo<Evaluation> selectPage(PageQuery pageQuery) {
        return BasePageService.pageQuery(() ->
                        // 仅传 name / patientId / nurseId 给 mapper
                        evaluationMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName() , pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord()),
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }

}
