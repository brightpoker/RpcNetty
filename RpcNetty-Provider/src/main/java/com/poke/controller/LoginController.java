package com.poke.controller;

import com.alibaba.fastjson.JSONObject;
import com.poke.entity.InfoUser;
import com.poke.service.InfoUserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * @ClassName LoginController
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 11:16 下午
 */
@Controller
@Log4j
public class LoginController {

    @Autowired
    InfoUserService userService;

    @RequestMapping("/getById")
    @ResponseBody
    public InfoUser getById(String id) {
        log.info("根据ID查询用户信息:" + id);
        return userService.getInfoUserById(id);
    }

    @RequestMapping("/getNameById")
    @ResponseBody
    public String getNameById(String id) {
        log.info("根据ID查询用户名称:" + id);
        return userService.getNameById(id);
    }

}
