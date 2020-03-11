package com.poke.controller;

import com.poke.entity.InfoUser;
import com.poke.service.InfoUserService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName UserController
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 1:03 上午
 */
@Controller
@Log4j
public class UserController {
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