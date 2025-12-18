package com.example.mapper;

import com.example.entity.DailyMonitor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DailyMonitorMapper {

    //新增
    int insert(DailyMonitor dailyMonitor);

    //删除
    @Delete("delete from dailymonitor where id=#{id}")
    int deleteById(Integer id);

    //更新
    int updateById(DailyMonitor dailyMonitor);

    //根据id查询
    @Select("select * from dailymonitor where id=#{id}")
    DailyMonitor selectById(Integer id);

    //查询所有
    List<DailyMonitor> selectAll(@Param("name") String name,
                                 @Param("nurseName") String nurseName,
                                 @Param("patientId") Integer patientId,
                                 @Param("nurseId") Integer nurseId,
                                 @Param("record") String record);
}
