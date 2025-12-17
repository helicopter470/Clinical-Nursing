package com.example.mapper;

import com.example.entity.NurseService;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NurseServiceMapper {

    //新增
    int insert(NurseService nurseService);

    //删除
    @Delete("delete from nurseService where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(NurseService nurseService);

    //根据id查询
    @Select("select * from nurseService where id=#{id}")
    NurseService selectById(Integer id);

    //查询所有
    List<NurseService> selectAll(NurseService nurseService);

    @Select("select * from nurseService where username=#{username}")
    NurseService selectByUsername(String username);
}
