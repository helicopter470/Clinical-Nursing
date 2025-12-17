package com.example.controller;

import com.example.common.Result;
import com.example.entity.CareKnowledge;
import com.example.service.CareKnowledgeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//管理员前端接口
@RestController
@RequestMapping("/careKnowledge")
public class CareKnowledgeController {

    @Resource
    private CareKnowledgeService careKnowledgeService;
    private Result success;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody CareKnowledge careKnowledge) {
        careKnowledgeService.add(careKnowledge);
        return Result.success();
    }

    //删除
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        careKnowledgeService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        careKnowledgeService.deleteBatch(ids);
        return Result.success();
    }

    //更新
    @PutMapping("/update")
    public Result updateById(@RequestBody CareKnowledge careKnowledge) {
        careKnowledgeService.updateById(careKnowledge);
        return Result.success();
    }

    //根据id查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        CareKnowledge careKnowledge = careKnowledgeService.selectById(id);
        return Result.success(careKnowledge);
    }

    //查询所有
    @GetMapping("/selectAll")
    public Result selectAll(CareKnowledge careKnowledge) {
        List<CareKnowledge> list = careKnowledgeService.selectAll(careKnowledge);
        return Result.success(list);
    }

    //分页查询
    @GetMapping("/selectPage")
    public Result selectPage(CareKnowledge careKnowledge,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<CareKnowledge> page=careKnowledgeService.selectPage(careKnowledge,pageNum,pageSize);
        return Result.success(page);
    }
    // 增加阅读量（前端点击后调用）
    @PostMapping("/incrementClick/{id}")
    public Result incrementClick(@PathVariable Integer id) {
        boolean ok = careKnowledgeService.incrementClick(id);
        if (ok) return Result.success();
        return Result.error("更新阅读量失败");
    }
}
