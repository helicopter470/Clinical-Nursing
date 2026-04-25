package com.example.controller;


import com.example.common.Result;
import com.example.dto.ChangePasswordRequest;
import com.example.entity.Account;
import com.example.entity.LoginResponse;
import com.example.entity.Patient;
import com.example.service.AdminService;
import com.example.service.NurseService;
import com.example.service.PatientService;
import com.example.security.JwtService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Resource
    private PatientService patientService;

    @Resource
    private AdminService adminService;

    @Resource
    private NurseService nurseService;

    private final JwtService jwtService;

    public WebController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

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
        if (dbaccount == null) {
            return Result.error("未知角色");
        }

        String token = jwtService.generateAccessToken(dbaccount.getId(), dbaccount.getUsername(), dbaccount.getRole());
        return Result.success(new LoginResponse(dbaccount, token));
    }

    //注册接口
    @PostMapping("/register")
    public Result register(@RequestBody Patient patient){
        patientService.register(patient);
        return Result.success();
    }

    /**
     * 更安全的修改密码：要求已登录（JwtAuthFilter 会校验 Token），并校验旧密码。
     * 修改成功后建议前端清理 Token 并重新登录。
     */
    @PutMapping("/changePassword")
    public Result changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                 @RequestAttribute(value = "uid", required = false) Object uid) {
        if (uid == null) {
            return Result.unauthorized("未登录");
        }
        if (request.getUserId() == null || !String.valueOf(request.getUserId()).equals(String.valueOf(uid))) {
            return Result.unauthorized("无权限修改他人密码");
        }

        if ("ADMIN".equals(request.getRole())) {
            adminService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        } else if ("NURSE".equals(request.getRole())) {
            nurseService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        } else if ("PATIENT".equals(request.getRole())) {
            patientService.changePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
        } else {
            return Result.error("未知角色");
        }
        return Result.success("密码修改成功，请重新登录");
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