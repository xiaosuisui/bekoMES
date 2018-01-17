package com.mj.beko.web.rest;

import com.mj.beko.constants.BekoImsConstants;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.domain.User;
import com.mj.beko.domain.UserVm;
import com.mj.beko.repository.UserRepository;
import com.mj.beko.security.SecurityUtils;
import com.mj.beko.service.MenuService;
import com.mj.beko.service.UserService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/8/15.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class UserResource {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuService menuService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 保存用户
     * @param user
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserVm user) throws URISyntaxException {
        log.debug("REST request to save User : {}", user);
        if (user.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new user cannot already have an ID")).body(null);
        } else if (userRepository.findOneByLogin(user.getLogin()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", "Login already in use")).body(null);
        } else if (userRepository.findOneByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "emailexists", "Email already in use")).body(null);
        } else {
            User newUser = userService.createUser(user);
            return new ResponseEntity<User>(newUser, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
        }
    }

/*
    */
/**
     * 分页查询
     * @return
     *//*

    @GetMapping("/users")
    public ResponseEntity<List<User>> queryByPage(PageUtil pageUtil) {
        log.debug("REST request to get a page of Orders");
        return new ResponseEntity<List<User>>( userService.queryByPage(pageUtil.getPage(), pageUtil.getSize()), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
*/
   //带参数的查询分页
    @GetMapping("/users")
    public ResponseEntity<List<User>> orderByPage(int page, int size, String loginName, String email){
        log.info("用户带参数的分页查询,{}{}",page,loginName);
        Page<User> userPage =userService.findAllByPageAndCondition(loginName,email,page,size);
        List<User> users=userPage.getContent();
        return new ResponseEntity<List<User>>(users,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllUsersByCondition")
    public long getCountByCondition(String login,String email){
        log.info("用户模块带参数的查询总记录数{}",login);
        long result =userService.getAllUserCountByCondition(login,email);
        log.info("返回的结果为{}",result);
        return result;
    }

    /**
     * 删除用户
     * @param login
     * @return
     */
    @DeleteMapping("/users/{login:" + BekoImsConstants.LOGIN_REGEX + "}")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("user.deleted", login)).build();
    }

    /**
     * 前端调用获取系统用户名
     * @return
     */
    @GetMapping("/account")
    public ResponseEntity<User> getAccount() {
        log.info("获取当前登录的用户名");
        return new ResponseEntity<User>(userService.getUserWithAuthorities(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 获取用户的对应的menu访问权限
     * @return
     */
    @GetMapping("/getUserAccessUrls")
    public ResponseEntity<List<String>> getUserAccessUrls(){
        return new ResponseEntity<List<String>>(menuService.getUserAccessUrls(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 获取总记录数
     * @return
     */
    @GetMapping("/getAllUserCount")
    public ResponseEntity<String> getAllUserCount(){
        log.debug("REST request to get all user count");
        return new ResponseEntity<String>(userService.getAllUserCount(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 通过用户名，返回实体
     * @param login
     * @return
     */
    @GetMapping("/users/{login:" + BekoImsConstants.LOGIN_REGEX + "}")
    public ResponseEntity<User> getUser(@PathVariable String login){
        log.info("request to get a User by{}", login);
        return new ResponseEntity<User>(userService.getUser(login).get(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * update
     * @param user
     * @return
     */
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody UserVm user) {
        log.debug("REST request to update User : {}", user);
        Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(user.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "emailexists", "Email already in use")).body(null);
        }
        existingUser = userRepository.findOneByLogin(user.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(user.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", "Login already in use")).body(null);
        }
        Optional<User> updatedUser = userService.update(user);
        return ResponseUtil.wrapOrNotFound(updatedUser, HeaderUtil.createAlert("user.updated", user.getLogin()));
    }

    @PostMapping("/account")
    public ResponseEntity <User> updateAccount(@RequestBody User user){
           Optional<User> updateUser = userService.updateAccount(user);
        return ResponseUtil.wrapOrNotFound(updateUser, HeaderUtil.createAlert("user.updated", user.getLogin()));
    }

    /**
     * 登录用户名的唯一性检测
     * @param request
     * @return
     */
    @PostMapping("/check/login")
    public ResponseEntity<Boolean> checkLogin(HttpServletRequest request){
        String login = request.getParameter("value");
        log.info("check login::{}", login);
        Optional<User> user = userRepository.findOneByLogin(login);
        return new ResponseEntity<Boolean>(!user.isPresent(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    @GetMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(HttpServletRequest request){
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String userName = SecurityUtils.getCurrentUserLogin();
        Optional<User> user = userService.getUser(userName);
        User user1 = user.get();
        String pwd = user1.getPassword();
        Boolean right = passwordEncoder.matches(oldPassword, pwd);
        String encoderNewPassword = passwordEncoder.encode(newPassword);
        Boolean result;
        if(right){
            user1.setPassword(encoderNewPassword);
            userService.updateAccount(user1);
            log.info("密码修改成功");
            result = true;
            return new ResponseEntity<Boolean>(result, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
        }else{
            log.info("输入密码与原密码不一致");
            result = false;
            return new ResponseEntity<Boolean>(result, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
        }
    }
}
