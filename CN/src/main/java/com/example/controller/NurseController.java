package com.example.controller;

import com.example.common.Result;
import com.example.entity.Nurse;
import com.example.service.NurseService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    @Resource
    private NurseService nurseService;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Nurse nurse) {
        nurseService.add(nurse);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        nurseService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        nurseService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody Nurse nurse) {
        nurseService.updateById(nurse);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Nurse nurse = nurseService.selectById(id);
        return Result.success(nurse);
    }

    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(Nurse nurse) {
        List<Nurse> list =nurseService.selectAll(nurse);
        return Result.success(list);
    }

    //分页查询
    @GetMapping("/selectPage")
    public Result selectPage(Nurse nurse,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<Nurse> page = nurseService.selectPage(nurse,pageNum,pageSize);
        return Result.success(page);
    }

}
