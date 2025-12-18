package com.example.mapper;

import com.example.entity.CareKnowledge;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CareKnowledgeMapper {

    //新增
    int insert(CareKnowledge careKnowledge);

    //删除
    @Delete("delete from careknowledge where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(CareKnowledge careKnowledge);

    //根据id查询
    @Select("select * from careknowledge where id=#{id}")
    CareKnowledge selectById(Integer id);

    //查询所有
    List<CareKnowledge> selectAll(CareKnowledge careKnowledge);

    // 增加阅读量（原子更新）
    @Update("update careknowledge set click_num = click_num + 1 where id = #{id}")
    int incrementClick(Integer id);
}
