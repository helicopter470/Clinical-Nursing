package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.Nurse;
import com.example.exception.CustomException;
import com.example.mapper.NurseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NurseService {

    @Resource
    private NurseMapper nurseMapper;

    //新增
    public void add(Nurse nurse) {
        Nurse dbnurse=nurseMapper.selectByUsername(nurse.getUsername());
        if(ObjectUtil.isNotNull(dbnurse)){
            throw new CustomException("用户已存在");
        }
        if(ObjectUtil.isEmpty(nurse.getPassword())){
            nurse.setPassword("123456");
        }
        if(ObjectUtil.isEmpty(nurse.getName())){
            nurse.setName(nurse.getUsername());
        }
        nurse.setRole("NURSE");
        nurseMapper.insert(nurse);
    }

    //删除
    public void deleteById(Integer id) {
        nurseMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    //更新
    public void updateById(Nurse nurse) {
        nurseMapper.updateById(nurse);
    }

    //根据id查询
    public Nurse selectById(Integer id) {
        return nurseMapper.selectById(id);
    }

    //查询所有
    public List<Nurse> selectAll(Nurse nurse) {
        List<Nurse> list = nurseMapper.selectAll(nurse);
        return list;
    }

    //分页查询
    public PageInfo<Nurse> selectPage(Nurse nurse,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Nurse> list = nurseMapper.selectAll(nurse);
        return PageInfo.of(list);
    }

    //登录接口
    public Account login(Account account){
        Account dbNurse=nurseMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbNurse)){
            throw new CustomException("用户不存在");
        }
        if(!account.getPassword().equals(dbNurse.getPassword())){
            throw new CustomException("账号或密码错误");
        }
        return dbNurse;
    }

    public void updatePassword(Account account){
        Nurse dbNurse=nurseMapper.selectByUsername(account.getUsername());
        if(ObjectUtil.isNull(dbNurse)){
            throw new CustomException("用户不存在");
        }
        dbNurse.setPassword(account.getPassword());
        nurseMapper.updateById(dbNurse);
    }
}
