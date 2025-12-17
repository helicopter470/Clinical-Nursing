package com.example.mapper;

import com.example.entity.Ward;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WardMapper {

    //新增
    int insert(Ward ward);

    //删除
    @Delete("delete from ward where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(Ward ward);

    //根据id查询
    @Select("select * from ward where id=#{id}")
    Ward selectById(Integer id);

    //查询所有
    List<Ward> selectAll(Ward ward);

    @Select("select * from ward where username=#{username}")
    Ward selectByUsername(String username);
}
