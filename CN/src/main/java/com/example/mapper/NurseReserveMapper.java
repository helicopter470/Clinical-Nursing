package com.example.mapper;

import com.example.entity.NurseReserve;
import com.example.entity.Patient;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NurseReserveMapper {

    //新增
    int insert(NurseReserve nurseReserve);

    //删除
    @Delete("delete from nurseReserve where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(NurseReserve nurseReserve);

    //根据id查询
    @Select("select * from nurseReserve where id=#{id}")
    NurseReserve selectById(Integer id);

    //查询所有
    List<NurseReserve> selectAll(@Param("name") String name,
                                 @Param("nurseName") String nurseName,
                                 @Param("patientId") Integer patientId,
                                 @Param("nurseId") Integer nurseId,
                                 @Param("record") String record);
    // 查询某护工正在服务中的患者列表
    List<Patient> selectPatientsByNurseInService(@Param("nurseId") Integer nurseId);

    // 按患者 id 查询该患者的所有护工预约记录
    @Select("select * from nurseReserve where patient_id = #{patientId}")
    List<NurseReserve> selectByPatientId(Integer patientId);
}
