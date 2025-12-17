package com.example.mapper;

import com.example.entity.Hp;
import com.example.entity.ServiceReserve;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HpMapper {

    //新增
    int insert(Hp hp);

    //删除
    @Delete("delete from hp where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(Hp hp);

    //根据id查询
    @Select("select * from hp where id=#{id}")
    Hp selectById(Integer id);

    //查询所有
    List<Hp> selectAll(@Param("name") String name,
                       @Param("nurseName") String nurseName,
                       @Param("patientId") Integer patientId,
                       @Param("nurseId") Integer nurseId,
                       @Param("record") String record);

    // 按患者 id 查询该患者的住院记录
    @Select("select * from hp where patient_id = #{patientId}")
    List<Hp> selectByPatientId(Integer patientId);
}
