package com.mj.beko.service.impl;

import com.mj.beko.domain.Menu;
import com.mj.beko.domain.User;
import com.mj.beko.repository.MenuRepository;
import com.mj.beko.repository.UserRepository;
import com.mj.beko.security.SecurityUtils;
import com.mj.beko.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/17.
 */
@Service
@Transactional
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<String> getUserAccessUrls() {
        Optional<User> sysUserOptional = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        List<String> urls = new ArrayList<>();
        if (sysUserOptional.isPresent()) {
            User sysUser = sysUserOptional.get();
            /*通过用户名查询对应得权限列表*/
           Optional<List<Menu>> menusOptions = menuRepository.findMenuNameByLoginId(sysUser.getId());
           if (menusOptions.isPresent() && menusOptions.get().size()>0){
               List<Menu> menus = menusOptions.get();
               menus.stream().forEach(menu -> urls.add(menu.getUrl()));
           }
        }
        return urls;
    }

    @Override
    public Menu findOne(Long id) {
        log.info("Find Menu By Id, ID={}", id);
        return menuRepository.findOne(id);
    }

    @Override
    public Menu findOneByUrl(String url) {
        log.info("通过url查找menu");
        return menuRepository.findOneByUrl(url);
    }

    @Override
    public Menu save(Menu menu) {
        return null;
    }

    @Override
    public void delete(Menu menu) {

    }

    @Override
    public List<Menu> query() {
        return null;
    }

    @Override
    public List<Menu> queryByPage(int page, int size) {
        log.info("Query menu by page, page={}, size={}", page, size);
        return menuRepository.queryByPage(page * size, size);
    }
}
