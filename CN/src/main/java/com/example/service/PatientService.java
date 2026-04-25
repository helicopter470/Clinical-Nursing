package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.PasswordPolicy;
import com.example.entity.Account;
import com.example.entity.Patient;
import com.example.mapper.PatientMapper;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Service
public class PatientService {

    @Resource
    private PatientMapper patientMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    //新增
    public void add(Patient patient) {
        Patient dbPatient=patientMapper.selectByUsername(patient.getUsername());
        if(ObjectUtil.isNotNull(dbPatient)){
            throw new CustomException("用户已存在");
        }
        if(ObjectUtil.isEmpty(patient.getPassword())){
            patient.setPassword("123456");
        }
        if(ObjectUtil.isEmpty(patient.getName())){
            patient.setName(patient.getUsername());
        }
        patient.setRole("PATIENT");
        // 新增用户时就开始加密存储
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientMapper.insert(patient);
    }

    //删除
    public void deleteById(Integer id) {
        patientMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(Patient patient) {
        patientMapper.updateById(patient);
    }

    //根据id查询
    public Patient selectById(Integer id) {
        return patientMapper.selectById(id);
    }

    //查询所有
    public List<Patient> selectAll(Patient patient) {
        List<Patient> list = patientMapper.selectAll(patient);
        return list;
    }

    //分页查询
    public PageInfo<Patient> selectPage( Patient patient,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Patient> list=patientMapper.selectAll(patient);
        return PageInfo.of(list);
    }

    //登录方法
    public Account login(Account account){
        Account dbPatient=patientMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbPatient)){
            throw new CustomException("用户不存在");
        }
        String raw = account.getPassword();
        String stored = dbPatient.getPassword();
        boolean ok;
        try {
            ok = stored != null && stored.startsWith("$2") ? passwordEncoder.matches(raw, stored) : raw.equals(stored);
        } catch (Exception e) {
            ok = raw.equals(stored);
        }
        if (!ok) {
            throw new CustomException("账号或密码错误");
        }
        // 存量明文密码：登录成功后可自动升级为 BCrypt
        if (stored != null && !stored.startsWith("$2")) {
            Patient p = patientMapper.selectByUsername(account.getUsername());
            if (p != null) {
                p.setPassword(passwordEncoder.encode(raw));
                patientMapper.updateById(p);
                dbPatient.setPassword(p.getPassword());
            }
        }
        return dbPatient;
    }

    //注册方法
    public void register(Patient patient){
        this.add(patient);
    }

    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        Patient dbPatient = patientMapper.selectById(userId);
        if (ObjectUtil.isNull(dbPatient)) {
            throw new CustomException("用户不存在");
        }
        PasswordPolicy.validateNewPassword(newPassword, oldPassword);

        String stored = dbPatient.getPassword();
        boolean ok;
        try {
            ok = stored != null && stored.startsWith("$2") ? passwordEncoder.matches(oldPassword, stored) : oldPassword.equals(stored);
        } catch (Exception e) {
            ok = oldPassword.equals(stored);
        }
        if (!ok) {
            throw new CustomException("旧密码不正确");
        }

        dbPatient.setPassword(passwordEncoder.encode(newPassword));
        patientMapper.updateById(dbPatient);
    }

    public void updatePassword(Account account){
        Patient dbPatient=patientMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbPatient)){
            throw new CustomException("用户不存在");
        }
        // 兼容旧接口：直接重置为传入密码（仍改为加密存储）
        dbPatient.setPassword(passwordEncoder.encode(account.getPassword()));
        patientMapper.updateById(dbPatient);
    }
}
