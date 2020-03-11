package com.poke.entity;

import lombok.Data;

/**
 * @ClassName Request
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 2:54 上午
 */
@Data
public class Request {
    private String id;
    private String className;// 类名
    private String methodName;// 函数名称
    private Class<?>[] parameterTypes;// 参数类型
    private Object[] parameters;// 参数列表
}
