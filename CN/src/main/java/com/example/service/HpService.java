package com.example.service;

import com.example.entity.Hp;
import com.example.entity.PageQuery;
import com.example.exception.CustomException;
import com.example.mapper.HpMapper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class HpService {

    @Resource
    private HpMapper hpMapper;

    private static final String STATUS_ADMITTED = "住院中";
    private static final String STATUS_DISCHARGED = "已出院";

    //新增
    @Transactional
    public void add(Hp hp){
        validateHpForAdd(hp);
        hpMapper.insert(hp);
    }

    //删除
    public void deleteById(Integer id){ hpMapper.deleteById(id);}

    public void deleteBatch(List<Integer> ids){
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    //更新
    @Transactional
    public void updateById(Hp hp){
        validateHpForUpdate(hp);
        hpMapper.updateById(hp);
    }

    //根据id查询
    public Hp selectById(Integer id){
        return hpMapper.selectById(id);
    }

    public List<Hp> selectAll(PageQuery pageQuery) {
        return hpMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord());
    }

    // 通用分页接口：注意不要把 pageNum/pageSize 传入 mapper，分页由 PageHelper 控制
    public PageInfo<Hp> selectPage(PageQuery pageQuery) {
        return BasePageService.pageQuery(() ->
                        // 仅传 name / patientId / nurseId 给 mapper
                        hpMapper.selectAll(pageQuery.getName(), pageQuery.getNurseName(), pageQuery.getPatientId(), pageQuery.getNurseId(),pageQuery.getRecord()),
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    // 根据 patientId 查询该患者的所有住院记录
    public List<Hp> selectByPatientId(Integer patientId) {
        return hpMapper.selectByPatientId(patientId);
    }

    // =========================
    // 住院业务校验
    // =========================

    private void validateHpForAdd(Hp hp) {
        if (hp == null) {
            throw new CustomException("请求不能为空");
        }
        normalizeAndValidateBasic(hp);

        // 新增默认住院中（如果前端没传）
        if (hp.getStatus() == null || hp.getStatus().trim().isEmpty()) {
            hp.setStatus(STATUS_ADMITTED);
        }
        validateStatusValue(hp.getStatus());

        LocalDate admit = parseDateRequired(hp.getAdmitDate(), "入院日期不能为空");
        LocalDate discharge = parseDateOptional(hp.getDischargeDate(), "出院日期格式错误，需为 YYYY-MM-DD");

        // 住院中：出院日期必须为空
        if (STATUS_ADMITTED.equals(hp.getStatus())) {
            if (discharge != null) {
                throw new CustomException("住院中状态下不允许填写出院日期");
            }
            // 同患者只能有一条在院记录
            Hp existed = hpMapper.selectAdmittedByPatientId(hp.getPatientId());
            if (existed != null) {
                throw new CustomException("该患者已有住院中记录，无法重复住院");
            }
            // 同病房床号在院唯一
            Hp bedUsed = hpMapper.selectAdmittedByWardAndBed(hp.getWardId(), hp.getBedNum());
            if (bedUsed != null) {
                throw new CustomException("该病房床号已被占用，请更换床号");
            }
        }

        // 已出院：必须有出院日期且不早于入院日期
        if (STATUS_DISCHARGED.equals(hp.getStatus())) {
            if (discharge == null) {
                throw new CustomException("已出院状态必须填写出院日期");
            }
            if (discharge.isBefore(admit)) {
                throw new CustomException("出院日期不能早于入院日期");
            }
        }
    }

    private void validateHpForUpdate(Hp hp) {
        if (hp == null || hp.getId() == null) {
            throw new CustomException("id不能为空");
        }
        Hp db = hpMapper.selectById(hp.getId());
        if (db == null) {
            throw new CustomException("住院记录不存在");
        }

        normalizeAndValidateBasic(hp);
        if (hp.getStatus() == null || hp.getStatus().trim().isEmpty()) {
            // 不允许把 status 更新为空
            hp.setStatus(db.getStatus());
        }
        validateStatusValue(hp.getStatus());

        LocalDate admit = parseDateRequired(hp.getAdmitDate(), "入院日期不能为空");
        LocalDate discharge = parseDateOptional(hp.getDischargeDate(), "出院日期格式错误，需为 YYYY-MM-DD");

        // “住院中”状态下禁止填出院日期
        if (STATUS_ADMITTED.equals(hp.getStatus())) {
            if (discharge != null) {
                throw new CustomException("住院中状态下不允许填写出院日期");
            }
        }

        // “已出院”必须有出院日期且不早于入院日期
        if (STATUS_DISCHARGED.equals(hp.getStatus())) {
            if (discharge == null) {
                throw new CustomException("已出院状态必须填写出院日期");
            }
            if (discharge.isBefore(admit)) {
                throw new CustomException("出院日期不能早于入院日期");
            }
        }

        // 若更新为住院中：需要校验唯一性，但要排除自己
        if (STATUS_ADMITTED.equals(hp.getStatus())) {
            Hp existed = hpMapper.selectAdmittedByPatientId(hp.getPatientId());
            if (existed != null && !existed.getId().equals(hp.getId())) {
                throw new CustomException("该患者已有住院中记录，无法重复住院");
            }
            Hp bedUsed = hpMapper.selectAdmittedByWardAndBed(hp.getWardId(), hp.getBedNum());
            if (bedUsed != null && !bedUsed.getId().equals(hp.getId())) {
                throw new CustomException("该病房床号已被占用，请更换床号");
            }
        }
    }

    private void normalizeAndValidateBasic(Hp hp) {
        if (hp.getPatientId() == null) {
            throw new CustomException("患者不能为空");
        }
        if (hp.getWardId() == null) {
            throw new CustomException("病房不能为空");
        }
        if (hp.getBedNum() == null || hp.getBedNum().trim().isEmpty()) {
            throw new CustomException("床号不能为空");
        }
        if (hp.getAdmitDate() == null || hp.getAdmitDate().trim().isEmpty()) {
            throw new CustomException("入院日期不能为空");
        }
        // 其它字段如 illness/illnessDesc/treatment 是否必填由前端表单决定；后端不强制也可。
    }

    private void validateStatusValue(String status) {
        if (!STATUS_ADMITTED.equals(status) && !STATUS_DISCHARGED.equals(status)) {
            throw new CustomException("住院状态仅允许：住院中 / 已出院");
        }
    }

    private LocalDate parseDateRequired(String dateStr, String emptyMsg) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new CustomException(emptyMsg);
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new CustomException("日期格式错误，需为 YYYY-MM-DD");
        }
    }

    private LocalDate parseDateOptional(String dateStr, String invalidMsg) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new CustomException(invalidMsg);
        }
    }
}
