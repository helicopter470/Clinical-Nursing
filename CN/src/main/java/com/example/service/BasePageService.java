package com.example.service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.function.Supplier;
public class BasePageService {
    /**
     * 通用分页工具：传入一个 Supplier（执行具体 mapper 查询），并返回 PageInfo
     */
    public static <T> PageInfo<T> pageQuery(Supplier<List<T>> supplier, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = supplier.get();
        return new PageInfo<>(list);
    }
}
