package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.PasswordPolicy;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    //新增
    public void add(Admin admin){
        Admin dbadmin=adminMapper.selectByUsername(admin.getUsername());
        if(ObjectUtil.isNotNull(dbadmin)){
            throw new CustomException("用户已存在");
        }
        if(ObjectUtil.isEmpty(admin.getPassword())){
            admin.setPassword("admin");
        }
        if(ObjectUtil.isEmpty(admin.getName())){
            admin.setName(admin.getUsername());
        }
        admin.setRole("ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminMapper.insert(admin);
    }

    //删除
    public void deleteById(Integer id){ adminMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(Admin admin){
        adminMapper.updateById(admin);
    }

    //根据id查询
    public Admin selectById(Integer id){
        return adminMapper.selectById(id);
    }

    //查询所有
    public List<Admin> selectAll(Admin admin){
        List<Admin> list =adminMapper.selectAll(admin);
        return list;
    }

    //分页查询
    public PageInfo<Admin> selectPage(Admin admin,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    //登录
    public Account login(Account account){
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbAdmin)){
            throw new CustomException("用户不存在");
        }
        String raw = account.getPassword();
        String stored = dbAdmin.getPassword();
        boolean ok;
        try {
            ok = stored != null && stored.startsWith("$2") ? passwordEncoder.matches(raw, stored) : raw.equals(stored);
        } catch (Exception e) {
            ok = raw.equals(stored);
        }
        if(!ok){
            throw new CustomException("账号或密码错误");
        }
        // 存量明文密码：登录成功后自动升级
        if (stored != null && !stored.startsWith("$2")) {
            dbAdmin.setPassword(passwordEncoder.encode(raw));
            adminMapper.updateById(dbAdmin);
        }
        return dbAdmin;
    }

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        Admin dbAdmin = adminMapper.selectById(userId);
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException("用户不存在");
        }
        PasswordPolicy.validateNewPassword(newPassword, oldPassword);

        String stored = dbAdmin.getPassword();
        boolean ok;
        try {
            ok = stored != null && stored.startsWith("$2") ? passwordEncoder.matches(oldPassword, stored) : oldPassword.equals(stored);
        } catch (Exception e) {
            ok = oldPassword.equals(stored);
        }
        if (!ok) {
            throw new CustomException("旧密码不正确");
        }
        dbAdmin.setPassword(passwordEncoder.encode(newPassword));
        adminMapper.updateById(dbAdmin);
    }

    public void updatePassword(Account account){
        Admin dvAdmin=adminMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dvAdmin)){
            throw new CustomException("用户不存在");
        }
        dvAdmin.setPassword(passwordEncoder.encode(account.getPassword()));
        adminMapper.updateById(dvAdmin);
    }
}
