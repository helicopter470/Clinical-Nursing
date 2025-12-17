package com.example.controller;

import com.example.common.Result;
import com.example.entity.NurseReserve;
import com.example.entity.PageQuery;
import com.example.entity.Patient;
import com.example.entity.ServiceReserve;
import com.example.service.NurseReserveService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//管理员前端接口
@RestController
@RequestMapping("/nurseReserve")
public class NurseReserveController {

    @Resource
    private NurseReserveService nurseReserveService;
    private Result success;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody NurseReserve nurseReserve) {
        nurseReserveService.add(nurseReserve);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        nurseReserveService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        nurseReserveService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody NurseReserve nurseReserve) {
        nurseReserveService.updateById(nurseReserve);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        NurseReserve nurseReserve = nurseReserveService.selectById(id);
        return Result.success(nurseReserve);
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
        List<NurseReserve> list = nurseReserveService.selectAll(pageQuery);
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
        PageInfo<NurseReserve> page = nurseReserveService.selectPage(pageQuery);
        return Result.success(page);
    }

    // 按护工id返回正在服务中的患者列表
    @GetMapping("/patientsInService/{nurseId}")
    public Result selectPatientsInServiceByNurse(@PathVariable Integer nurseId) {
        List<Patient> list = nurseReserveService.selectPatientsByNurseInService(nurseId);
        return Result.success(list);
    }

    // 根据 patientId 查询该患者的所有护工预约
    @GetMapping("/selectByPatientId/{patientId}")
    public Result selectByPatientId(@PathVariable Integer patientId) {
        List<NurseReserve> list = nurseReserveService.selectByPatientId(patientId);
        return Result.success(list);
    }
}
