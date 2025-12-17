package com.example.mapper;

import com.example.entity.Evaluation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    //新增
    int insert(Evaluation evaluation);

    //删除
    @Delete("delete from evaluation where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(Evaluation evaluation);

    //根据id查询
    @Select("select * from evaluation where id=#{id}")
    Evaluation selectById(Integer id);

    //查询所有
    List<Evaluation> selectAll(@Param("name") String name,
                               @Param("nurseName") String nurseName,
                               @Param("patientId") Integer patientId,
                               @Param("nurseId") Integer nurseId,
                               @Param("record") String record);
}