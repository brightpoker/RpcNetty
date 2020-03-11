package com.poke.service;

import com.poke.entity.InfoUser;

import java.util.List;
import java.util.Map;

/**
 * @InterfaceName InfoUserService
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 2:53 上午
 */
public interface InfoUserService {

    InfoUser getInfoUserById(String id);

    String getNameById(String id);
}
