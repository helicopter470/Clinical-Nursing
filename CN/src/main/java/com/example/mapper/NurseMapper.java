package com.example.mapper;

import com.example.entity.Nurse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NurseMapper {

    //新增
    int insert(Nurse nurse);

    //删除
    @Delete("delete from nurse where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(Nurse nurse);

    //根据id查询
    @Select("select * from nurse where id=#{id}")
    Nurse selectById(Integer id);

    //查询所有
    List<Nurse> selectAll(Nurse nurse);

    @Select("select * from nurse where username=#{username}")
    Nurse selectByUsername(String username);
}
