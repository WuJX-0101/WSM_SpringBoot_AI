package com.wms.common.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * 
 * 所有Controller接口统一返回此类型
 * 前端根据 code 判断请求是否成功
 * 
 * 使用示例:
 * - 成功: R.ok(data) 或 R.ok(data, "操作成功")
 * - 失败: R.fail("错误信息") 或 R.fail(500, "错误信息")
 * 
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码：200成功，其他表示失败 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 成功（无数据） */
    public static <T> R<T> ok() {
        return restResult(null, 200, "操作成功");
    }

    /** 成功（有数据） */
    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "操作成功");
    }

    /** 成功（自定义消息） */
    public static <T> R<T> ok(T data, String message) {
        return restResult(data, 200, message);
    }

    /** 失败（默认500错误码） */
    public static <T> R<T> fail() {
        return restResult(null, 500, "操作失败");
    }

    /** 失败（自定义消息） */
    public static <T> R<T> fail(String message) {
        return restResult(null, 500, message);
    }

    /** 失败（自定义错误码和消息） */
    public static <T> R<T> fail(int code, String message) {
        return restResult(null, code, message);
    }

    /**
     * 构建响应结果
     * 使用泛型静态方法，支持链式调用
     */
    private static <T> R<T> restResult(T data, int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMessage(message);
        return r;
    }

    /** 判断是否成功 */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
