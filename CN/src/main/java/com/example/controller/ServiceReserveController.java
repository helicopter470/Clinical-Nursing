package com.example.controller;

import com.example.common.Result;
import com.example.entity.PageQuery;
import com.example.entity.ServiceReserve;
import com.example.service.ServiceReserveService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//管理员前端接口
@RestController
@RequestMapping("/serviceReserve")
public class ServiceReserveController {

    @Resource
    private ServiceReserveService serviceReserveService;

    //新增
    @PostMapping("/add")
    public Result add(@Valid @RequestBody ServiceReserve serviceReserve) {
        serviceReserveService.add(serviceReserve);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        serviceReserveService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        serviceReserveService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody ServiceReserve serviceReserve) {
        serviceReserveService.updateById(serviceReserve);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ServiceReserve serviceReserve = serviceReserveService.selectById(id);
        return Result.success(serviceReserve);
    }


    // 查询（非分页）
    @GetMapping("/selectAll")
    public Result selectAll(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String nurseName,
                            @RequestParam(required = false) Integer patientId,
                            @RequestParam(required = false) Integer nurseId,
                            @RequestParam(required = false) String record) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setName(name);
        pageQuery.setNurseName(nurseName);
        pageQuery.setPatientId(patientId);
        pageQuery.setNurseId(nurseId);
        pageQuery.setRecord(record);
        List<ServiceReserve> list = serviceReserveService.selectAll(pageQuery);
        return Result.success(list);
    }

    // 分页查询：构建 PageQuery 传入 Service
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String nurseName,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) Integer patientId,
                             @RequestParam(required = false) Integer nurseId,
                             @RequestParam(required = false) String record) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setName(name);
        pageQuery.setNurseName(nurseName);
        pageQuery.setPageNum(pageNum);
        pageQuery.setPageSize(pageSize);
        pageQuery.setPatientId(patientId);
        pageQuery.setNurseId(nurseId);
        pageQuery.setRecord(record);
        PageInfo<ServiceReserve> page = serviceReserveService.selectPage(pageQuery);
        return Result.success(page);
    }

    // 根据 patientId 查询该患者的所有预约
    @GetMapping("/selectByPatientId/{patientId}")
    public Result selectByPatientId(@PathVariable Integer patientId) {
        List<ServiceReserve> list = serviceReserveService.selectByPatientId(patientId);
        return Result.success(list);
    }
}
