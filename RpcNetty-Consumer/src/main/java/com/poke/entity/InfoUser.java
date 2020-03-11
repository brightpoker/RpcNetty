package com.poke.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName InfoUser
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 2:54 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoUser {
    private String id;
    private String name;
    private String address;
}
