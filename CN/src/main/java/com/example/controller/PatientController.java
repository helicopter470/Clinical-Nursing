package com.example.controller;

import com.example.common.Result;
import com.example.entity.Patient;
import com.example.service.PatientService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//患者前端操作接口
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Resource
    private PatientService patientService;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Patient patient) {
        patientService.add(patient);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        patientService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        patientService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Patient patient= patientService.selectById(id);
        return Result.success(patient);
    }

    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(Patient patient) {
        List<Patient> patients = patientService.selectAll(patient);
        return Result.success(patients);
    }

    //分页查询
    @GetMapping("selectPage")
    public Result selectPage(Patient patient,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<Patient> page=patientService.selectPage(patient,pageNum,pageSize);
        return Result.success(page);
    }
}
