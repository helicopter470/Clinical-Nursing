package com.example.controller;

import com.example.common.Result;
import com.example.entity.Ward;
import com.example.service.WardService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//管理员前端接口
@RestController
@RequestMapping("/ward")
public class WardController {

    @Resource
    private WardService wardService;
    private Result success;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Ward ward) {
        wardService.add(ward);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        wardService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        wardService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody Ward ward) {
        wardService.updateById(ward);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Ward ward = wardService.selectById(id);
        return Result.success(ward);
    }

    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(Ward ward) {
        List<Ward> list = wardService.selectAll(ward);
        return Result.success(list);
    }

    //分页查询
    @GetMapping("/selectPage")
    public Result selectPage(Ward ward,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Ward> page=wardService.selectPage(ward,pageNum,pageSize);
        return Result.success(page);
    }
}
