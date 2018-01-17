package com.mj.beko.service;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/18.
 */
public interface RoleService extends BaseService<Role>{

    /**
     * 查找所有的角色列表
     * @return
     */
    List<Role> findAllRole();

    /**
     * 通过Id查询角色
     * @param id
     * @return
     */
    Role findOne(Long id);

    List<Role> findRolesByUserId(Long Id);

    /**
     * 查询所有role
     * @return
     */
    List<Role> findAll();

    /**
     *  分页查询角色
     */
    Page<Role> findAll(Pageable pageable);

    /**
     *  Delete the "id" role.
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * 根据角色名查询角色信息
     * @param name
     * @return
     */
    Optional<Role> getRoleByName(String name);

    /**
     * 角色编号查询
     * @param roleNo
     * @return
     */
    Optional<Role> getRoleByRoleNo(String roleNo);

    Long getUserByRoleId(Long id);

    /**
     * 获取总记录
     * @return
     */
    String getAllCountRoles();

    /**
     * 通过ID查询
     * @param id
     * @return
     */
    Role getRoleById(Long id);
    Role saveAndFlush(Role role);

    Optional<Role> updateRole(Role role);

    /**
     * 条件查询角色总数量
     * @param roleNo
     * @param roleName
     * @return
     */
    long getAllCountByCondition(String roleNo,String roleName);

    /**
     * 条件查询角色
     * @param roleNo
     * @param roleName
     * @param page
     * @param size
     * @return
     */

    Page<Role> findAllRoleByPageAndCondition(String roleNo, String roleName, int page, int size);

}
