package com.mj.beko.security;

import com.mj.beko.domain.Menu;
import com.mj.beko.domain.User;
import com.mj.beko.repository.MenuRepository;
import com.mj.beko.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /*注入menu*/
    @Autowired
    private  MenuRepository menuRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        Optional<User> sysUser = userRepository.findOneByLogin(login);
        if (sysUser.isPresent()) {
            /*通过用户名查询对应得权限列表*/
            Optional<List<Menu>> menusOptions = menuRepository.findMenuNameByLoginId(sysUser.get().getId());
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            if(menusOptions.isPresent() && menusOptions.get().size() > 0){
                List<Menu> menus = menusOptions.get();
                //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
                menus.stream().forEach(menu -> {grantedAuthorities.add(new SimpleGrantedAuthority(menu.getName()));});
            }
            return new org.springframework.security.core.userdetails.User(sysUser.get().getLogin(), sysUser.get().getPassword(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("admin: " + login + " do not exist!");
        }
    }
}
