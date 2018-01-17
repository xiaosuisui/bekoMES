package com.mj.beko.service;

import com.mj.beko.domain.Menu;

import java.util.List;

/**
 * Created by xiaosui on 2017/6/29.
 */
public interface RoleMenuService {

    List<Menu> getSelectMenus(String roleId);

}
