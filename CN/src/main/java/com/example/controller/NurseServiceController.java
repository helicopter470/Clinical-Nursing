package com.example.controller;

import com.example.common.Result;
import com.example.entity.NurseService;
import com.example.service.NurseServiceService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//管理员前端接口
@RestController
@RequestMapping("/nurseService")
public class NurseServiceController {

    @Resource
    private NurseServiceService nurseServiceService;
    private Result success;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody NurseService nurseService) {
        nurseServiceService.add(nurseService);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        nurseServiceService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        nurseServiceService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody NurseService nurseService) {
        nurseServiceService.updateById(nurseService);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        NurseService nurseService = nurseServiceService.selectById(id);
        return Result.success(nurseService);
    }

    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(NurseService nurseService) {
        List<NurseService> list = nurseServiceService.selectAll(nurseService);
        return Result.success(list);
    }

    //分页查询
    @GetMapping("/selectPage")
    public Result selectPage(NurseService nurseService,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<NurseService> page=nurseServiceService.selectPage(nurseService,pageNum,pageSize);
        return Result.success(page);
    }
}
