package com.example.mapper;

import com.example.entity.Patient;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PatientMapper {

    //新增
    int insert(Patient patient);

    //删除
    @Delete("delete from patient where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(Patient patient);

    //根据id查询
    @Select("select * from patient where id=#{id}")
    Patient selectById(Integer id);

    //查询所有
    List<Patient> selectAll(Patient patient);

    @Select("select * from patient where username=#{username}")
    public Patient selectByUsername(String username);
}
