package com.example.mapper;

import com.example.entity.Admin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    //新增
    int insert(Admin admin);

    //删除
    @Delete("delete from admin where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(Admin admin);

    //根据id查询
    @Select("select * from admin where id=#{id}")
    Admin selectById(Integer id);

    //查询所有
    List<Admin> selectAll(Admin admin);

    @Select("select * from admin where username=#{username}")
    Admin selectByUsername(String username);
}
