package com.mj.beko.service.impl;

import com.mj.beko.domain.Role;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.domain.User;
import com.mj.beko.domain.UserVm;
import com.mj.beko.listener.PictureClearPublisher;
import com.mj.beko.repository.UserRepository;
import com.mj.beko.security.SecurityUtils;
import com.mj.beko.service.RoleService;
import com.mj.beko.service.UserService;
import com.mj.beko.util.DateTimeFormatUtil;
import com.mj.beko.util.StrOperatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by Ricardo on 2017/8/17.
 */
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PictureClearPublisher demoPublisher;

    @Override
    public User createUser(UserVm userVm) {
        User user = new User(userVm.getLogin(), userVm.getEmail(), userVm.getFirstName(), userVm.getLastName());
        Set<Role> roleSet = new HashSet<Role>();
        /*判断角色*/
        if(StrOperatorUtil.strIsNotBlank(userVm.getRoleIds())){
                Arrays.stream(userVm.getRoleIds().split(",")).forEach(roleId -> roleSet.add(roleService.findOne(Long.parseLong(roleId))));
                user.setRoles(roleSet);
        }
        /*判断图片*/
        if (StrOperatorUtil.strIsBlank(userVm.getImage())) {
            user.setImageUrl(null);
        } else {
            String path = request.getSession().getServletContext().getRealPath("upload");
            byte[] imageBytes = base64Decoding(userVm.getImage());
            String localPath = UUID.randomUUID() + "_" + userVm.getFileName();
            try{
                Files.write(Paths.get(path, localPath), imageBytes, StandardOpenOption.CREATE);
                user.setImageUrl(localPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*初始化密码123456*/
        user.setPassword(passwordEncoder.encode("123456"));
        user.setActivated(true);
        user.setLangKey("en");
        user.setCreatedDate(DateTimeFormatUtil.getCurrentDateTime());
        user.setCreatedBy(SecurityUtils.getCurrentUserLogin());
       //考虑要不要设置默认的权限
       return userRepository.save(user);
    }

    /**
     * 删除用户
     * @param login
     */
    @Override
    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            //删除用户数据
            userRepository.delete(user);
        });
    }

    /**
     * 获取总记录数
     * @return
     */
    @Override
    public String getAllUserCount() {
        return String.valueOf(userRepository.count());
    }

    /**
     * 用户名查询用户
     * @param login
     * @return
     */
    @Override
    public Optional<User> getUser(String login) {
        return userRepository.findOneByLogin(login);
    }

    /**
     * update User
     * @param userDTO
     * @return
     */
   /* @CacheEvict(value = "redis-foo",allEntries = true)*/
    @Override
    public Optional<User> update(UserVm userDTO) {
        return Optional.of(userRepository.findOne(userDTO.getId())).map(
                user -> {
                    if (StrOperatorUtil.strIsBlank(userDTO.getRoleIds())) {
                        user.setRoles(null);
                    } else {
                        Set<Role> roleSet = new HashSet<Role>();
                        Arrays.stream(userDTO.getRoleIds().split(",")).forEach(roleId -> roleSet.add(roleService.findOne(Long.parseLong(roleId))));
                        user.setRoles(roleSet);
                    }
                    //先判断当前的图片是否被修改
                    if (StrOperatorUtil.strIsBlank(userDTO.getImageUrl())) {
                        demoPublisher.publish(user.getImageUrl());
                       /*判断图片*/
                        if (StrOperatorUtil.strIsBlank(userDTO.getImage())) {
                            user.setImageUrl(null);
                        } else {
                            String path = request.getSession().getServletContext().getRealPath("upload");
                            byte[] imageBytes = base64Decoding(userDTO.getImage());
                            String localPath = UUID.randomUUID() + "_" + userDTO.getFileName();
                            try {
                                Files.write(Paths.get(path, localPath), imageBytes, StandardOpenOption.CREATE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            user.setImageUrl(localPath);
                        }
                    }
                    user.setLogin(userDTO.getLogin());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    user.setEmail(userDTO.getEmail());
                    user.setActivated(userDTO.isActivated());
                    user.setLastModifiedDate(DateTimeFormatUtil.getCurrentDateTime());
                    user.setModifyBy(SecurityUtils.getCurrentUserLogin());
                    return user;
                });
    }

    /**
     * 保存用户
     * @param user
     * @return
     */
    @Override
    public User save(User user) {
        log.info("保存用户操作{}", user);
        return userRepository.save(user);
    }

    /**
     * 删除用户
     * @param user
     */
    @Override
    public void delete(User user) {
        log.info("删除用户操作{}", user);
        userRepository.delete(user);
    }

    /**用户查询
     * @return
     */
    @Override
    public List<User> query() {
        log.info("查询所有用户");
        return userRepository.findAll();
    }

    /*@Cacheable( value = "redis-foo")*/
    /*@TargetDataSource("ds1")*/
    @Override
    public List<User> queryByPage(int page, int size) {
        log.info("分页查询User{}", page, size);
        return userRepository.queryByPage(page * size, size);
    }

    /**
     * 获取当前系统的登录用户名
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        log.info("前端调用,获取当前的系统登录用户名,{}", SecurityUtils.getCurrentUserLogin());
        Optional<User> user=userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).orElse(null);
    }

    private byte[] base64Decoding(String image) {
        if (StrOperatorUtil.strIsNotBlank(image)) {
            return Base64.getDecoder().decode(image.split("base64,")[1]);
        } else {
            return null;
        }
    }

    /**
     * 修改账户信息
     * @param user
     * @return
     */
    @Override
    public Optional<User> updateAccount(User user) {
        log.info("update user", user);
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(
                user1 -> {
                    user1.setFirstName(user.getFirstName());
                    user1.setLastName(user.getLastName());
                    user1.setEmail(user.getEmail());
                    user1.setPassword(user.getPassword());
                    return user1;
                }
        );
    }
    @Override
    public Page<User> findAllByPageAndCondition(String login, String email, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=login && !"".equals(login)&& !"null".equals(login) && !login.equals("undefined")) {
                    Predicate _login = criteriaBuilder.equal(root.get("login"), login);
                    predicates.add(_login);
                }
                if(null!=email && !"".equals(email)&& !"null".equals(email) &&!email.equals("undefined")){
                    Predicate _email = criteriaBuilder.equal(root.get("email"), email);
                    predicates.add(_email);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllUserCountByCondition(String login, String email) {
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=login && !"".equals(login) &&!"null".equals(login) && !login.equals("undefined") && !login.isEmpty()) {
                    Predicate _login= criteriaBuilder.equal(root.get("login"), login);
                    predicates.add(_login);
                }
                if(null!=email && !"".equals(email) && !"null".equals(email) && !email.equals("undefined") && !email.isEmpty()){
                    Predicate _email = criteriaBuilder.equal(root.get("email"), email);
                    predicates.add(_email);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return userRepository.count(specification);
    }

}
