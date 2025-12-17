package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Account;
import com.example.entity.Patient;
import com.example.mapper.PatientMapper;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientService {

    @Resource
    private PatientMapper patientMapper;

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
        if(!account.getPassword().equals(dbPatient.getPassword())){
            throw new CustomException("账号或密码错误");
        }
        return dbPatient;
    }

    //注册方法
    public void register(Patient patient){
        this.add(patient);
    }

    public void updatePassword(Account account){
        Patient dbPatient=patientMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbPatient)){
            throw new CustomException("用户不存在");
        }
        dbPatient.setPassword(account.getPassword());
        patientMapper.updateById(dbPatient);
    }
}
