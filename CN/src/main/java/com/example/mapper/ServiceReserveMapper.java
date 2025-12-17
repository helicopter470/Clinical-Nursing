package com.example.mapper;

import com.example.entity.ServiceReserve;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceReserveMapper {

    //新增
    int insert(ServiceReserve serviceReserve);

    //删除
    @Delete("delete from serviceReserve where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(ServiceReserve serviceReserve);

    //根据id查询
    @Select("select * from serviceReserve where id=#{id}")
    ServiceReserve selectById(Integer id);

    //查询所有
    List<ServiceReserve> selectAll(@Param("name") String name,
                                   @Param("nurseName") String nurseName,
                                   @Param("patientId") Integer patientId,
                                   @Param("nurseId") Integer nurseId,
                                   @Param("record") String record);

    //用于校验查询
    @Select("select * from servicereserve where patient_id = #{patientId}")
    List<ServiceReserve> selectByPatientId(Integer patientId);
}
