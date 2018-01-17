package com.mj.beko.service.impl;

import com.mj.beko.domain.Menu;
import com.mj.beko.repository.RoleMenuRepository;
import com.mj.beko.service.RoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Ricardo on 2017/6/29.
 */
@Service
@Transactional
@Slf4j
public class RoleMenuServiceImpl implements RoleMenuService {

    @Inject
    private RoleMenuRepository roleMenuRepository;

    @Override
    public List<Menu> getSelectMenus(String roleId) {
        return roleMenuRepository.getSelectMenus(roleId);
    }

}
