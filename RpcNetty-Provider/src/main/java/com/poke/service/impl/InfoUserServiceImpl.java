package com.poke.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.poke.annotation.RpcService;
import com.poke.entity.InfoUser;
import com.poke.service.InfoUserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName InfoUserServiceImpl
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 2:58 上午
 */
@RpcService
@Log4j
public class InfoUserServiceImpl implements InfoUserService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public InfoUser getInfoUserById(String id) {
        log.info("查询用户ID:" + id);
        String sql = "select * from info_user where id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<>(InfoUser.class));
    }

    @Override
    public String getNameById(String id) {
        log.info("根据ID查询用户名称:" + id);
        String sql = "select name from info_user where id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, new SingleColumnRowMapper<>(String.class));
    }
}
