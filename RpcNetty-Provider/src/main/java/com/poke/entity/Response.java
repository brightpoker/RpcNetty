package com.poke.entity;

import lombok.Data;

/**
 * @ClassName Response
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 2:55 上午
 */
@Data
public class Response {
    private String requestId;
    private int code;
    private String error_msg;
    private Object data;
}
