package com.mj.beko.service.impl;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.Role;
import com.mj.beko.repository.RoleRepository;
import com.mj.beko.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/18.
 */
@Service
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findAllRole() {
        log.info("request get all Role");
        return roleRepository.findAll();
    }

    /**
     * 通过Id查询角色
     * @param id
     * @return
     */
    @Override
    public Role findOne(Long id) {
        log.info("roleservice findOne ");
        return roleRepository.findOne(id);
    }

    /**
     * 用户Id查询角色
     * @param id
     * @return
     */
    @Override
    public List<Role> findRolesByUserId(Long id) {
        log.info("通过用户ID查询角色{}",id);
        return roleRepository.findRolesByUsers(id);
    }

    @Override
    public Role save(Role role) {
        log.debug("Request to save Role : {}", role);
        return roleRepository.save(role);
    }

    @Override
    public void delete(Role role) {

    }

    @Override
    public List<Role> query() {
        return null;
    }

    @Override
    public List<Role> queryByPage(int page, int size) {
        if (size == 0) {
            return roleRepository.findAll();
        }
        return roleRepository.queryByPage(page * size, size);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    /**
     *  查找所有role
     */
    @Override
    public Page<Role> findAll(Pageable pageable) {
        log.debug("Request to get all Roles");
        return roleRepository.findAll(pageable);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Role : {}", id);
        roleRepository.delete(id);
    }
    /**
     * 根据角色名查询角色信息
     * @param name
     * @return
     */
    @Override
    public Optional<Role> getRoleByName(String name) {
        log.debug("Request to get Role By name: {}", name);
        return roleRepository.getRoleByName(name);
    }

    @Override
    public Long getUserByRoleId(Long id) {
        log.debug("Request to get User By role id: {}", id);
        return roleRepository.getUserByRoleId(id);
    }
    @Override
    public String getAllCountRoles() {
        log.info("get role allCount");
        return String.valueOf(roleRepository.count());
    }

    @Override
    public Optional<Role> getRoleByRoleNo(String roleNo) {
        log.info("get Role by roleNo{}", roleNo);
       return roleRepository.getRoleByRoleNo(roleNo);
    }

    @Override
    public Role getRoleById(Long id) {
        log.info("通过Id查询role{}", id);
        return roleRepository.findOne(id);
    }

    @Override
    public Role saveAndFlush(Role role) {
        log.info("saveAndFlushrole");
        return roleRepository.saveAndFlush(role);
    }

    @Override
    public Optional<Role> updateRole(Role role) {
        return Optional.of(roleRepository.findOne(role.getId())).map(oldRole -> {
            oldRole.setRoleNo(role.getRoleNo());
            oldRole.setName(role.getName());
            oldRole.setRoleDesc(role.getRoleDesc());
            return oldRole;
        });
    }
    @Override
    public long getAllCountByCondition(String roleNo, String roleName) {
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=roleNo && !"".equals(roleNo) &&!"null".equals(roleNo) && !roleNo.equals("undefined") && !roleNo.isEmpty()) {
                    Predicate _roleNo = criteriaBuilder.equal(root.get("roleNo"), roleNo);
                    predicates.add(_roleNo);
                }
                if(null!=roleName && !"".equals(roleName) && !"null".equals(roleName) && !roleName.equals("undefined") && !roleName.isEmpty()){
                    Predicate _roleName = criteriaBuilder.equal(root.get("name"), roleName);
                    predicates.add(_roleName);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return roleRepository.count(specification);
    }

    @Override
    public Page<Role> findAllRoleByPageAndCondition(String roleNo, String roleName, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=roleNo && !"".equals(roleNo)&& !"null".equals(roleNo) && !roleNo.equals("undefined")) {
                    Predicate _oroleNo = criteriaBuilder.equal(root.get("roleNo"), roleNo);
                    predicates.add(_oroleNo);
                }
                if(null!=roleName && !"".equals(roleName)&& !"null".equals(roleName) &&!roleName.equals("undefined")){
                    Predicate _roleName = criteriaBuilder.equal(root.get("name"), roleName);
                    predicates.add(_roleName);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return roleRepository.findAll(specification, pageable);
    }
}
