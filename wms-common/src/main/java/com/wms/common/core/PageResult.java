package com.wms.common.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一分页结果封装类
 * 
 * 用于封装MyBatis-Plus的分页查询结果
 * 
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 每页大小 */
    private long size;

    /** 当前页码 */
    private long current;

    /** 总页数 */
    private long pages;

    public PageResult() {
    }

    /**
     * 构造函数
     * 
     * @param records 数据列表
     * @param total   总记录数
     * @param size    每页大小
     * @param current 当前页码
     */
    public PageResult(List<T> records, long total, long size, long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        // 计算总页数（向上取整）
        this.pages = (total + size - 1) / size;
    }
}
