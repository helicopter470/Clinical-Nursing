package com.example.controller;


import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Patient;
import com.example.service.AdminService;
import com.example.service.NurseService;
import com.example.service.PatientService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Resource
    private PatientService patientService;

    @Resource
    private AdminService adminService;

    @Resource
    private NurseService nurseService;

    //默认请求接口
    @GetMapping("/")
    public Result hello(){ return Result.success(); }

    //登录接口
    @PostMapping("/login")
    public Result login(@RequestBody Account account){
        Account dbaccount=null;
        if("ADMIN".equals(account.getRole())){
            dbaccount=adminService.login(account);
        }else if("NURSE".equals(account.getRole())){
            dbaccount=nurseService.login(account);
        }else if("PATIENT".equals(account.getRole())){
            dbaccount=patientService.login(account);
        }
        return Result.success(dbaccount);
    }

    //注册接口
    @PostMapping("/register")
    public Result register(@RequestBody Patient patient){
        patientService.register(patient);
        return Result.success();
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if ("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        }else if ("NURSE".equals(account.getRole())) {
            nurseService.updatePassword(account);
        }else if ("PATIENT".equals(account.getRole())) {
            patientService.updatePassword(account);
        }
        return Result.success();
    }

    @PostMapping("/verifyPassword")
    public Result verifyPassword(@RequestBody Account account) {
            if ("ADMIN".equals(account.getRole())) {
                adminService.login(account);
            } else if ("NURSE".equals(account.getRole())) {
                nurseService.login(account);
            } else if ("PATIENT".equals(account.getRole())) {
                patientService.login(account);
            } else {
                return Result.error("未知角色");
            }
            return Result.success();

    }
}
