package com.mj.beko.service;

import com.mj.beko.domain.TcsOrder;
import com.mj.beko.domain.User;
import com.mj.beko.domain.UserVm;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/17
 *
 */
public interface UserService extends BaseService<User>{
    /**
     * 获取验证用户
     * @return
     */
    User getUserWithAuthorities();

    /**
     * 创建用户
     */
    User createUser(UserVm user);

    /**
     * 通过用户名删除用户
     * @param login
     */
    void deleteUser(String login);

    /**
     * 获取总记录数
     * @return
     */
    String getAllUserCount();

    /**
     * 登录用户名查找用户
     * @param login
     * @return
     */
    Optional<User> getUser(String login);
    /**
     * update
     * @param t
     * @return
     */
    Optional<User> update(UserVm t);

    Optional<User> updateAccount(User user);
    /**
     * 分页条件查询用户信息
     * @param login
     * @param email
     * @param page
     * @param size
     * @return
     */
    Page<User> findAllByPageAndCondition(String login, String email, int page, int size);

    /**
     * 查询用户信息的总记录数
     * @param login
     * @param email
     * @return
     */
    long getAllUserCountByCondition(String login,String email);

}
