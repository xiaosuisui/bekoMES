package com.mj.beko.web.rest;

import com.mj.beko.domain.*;
import com.mj.beko.service.MenuService;
import com.mj.beko.service.RoleMenuService;
import com.mj.beko.service.RoleService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Ricardo on 2017/8/18.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class RoleResource {

    @Inject
    private RoleService roleService;

    @Inject
    private RoleMenuService roleMenuService;

    @Inject
    private MenuService menuService;

    private static final String ENTITY_NAME = "role";

    /**
     * 获取所有的角色
     * @return
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> roleByPage(int page, int size, String roleNo, String roleName){
        log.info("订单带参数的分页查询,{}{}",page,roleNo);
        Page<Role> roletPage =roleService.findAllRoleByPageAndCondition(roleNo,roleName,page,size);
        List<Role> roles=roletPage.getContent();
        return new ResponseEntity<List<Role>>(roles,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    /**
     * 通过用户Id查询角色
     * @param id
     * @return
     */
    @GetMapping("/getRolesByUserId")
    public ResponseEntity<List<Role>> getRolesByUserId(Long id){
        log.info("get roles By userId");
        return new ResponseEntity<List<Role>>(roleService.findRolesByUserId(id), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws URISyntaxException {
        log.debug("REST request to save Role : {}", role);
        if (role.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new role cannot already have an ID")).body(null);
        }
        Role result = roleService.save(role);
        return ResponseEntity.created(new URI("/api/roles/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRole(@PathVariable Long id) {
        log.debug("REST request to get Role : {}", id);
       return  new ResponseEntity<Role>(roleService.findOne(id), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    @PutMapping("/roles")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws URISyntaxException {
        log.debug("REST request to update Role : {}", role);
        if (role.getId() == null) {
            return createRole(role);
        }
        Optional<Role> result = roleService.updateRole(role);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, role.getId().toString()))
                .body(result.get());
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.debug("REST request to delete Role : {}", id);
        roleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    /**
     * 检查角色名的唯一性
     * @param request
     * @return
     */
    @PostMapping("/check/name")
    public ResponseEntity<Boolean> checkLogin(HttpServletRequest request){
        String roleName = request.getParameter("value");
        log.info("check role name::{}", roleName);
        Optional<Role> role = roleService.getRoleByName(roleName);
        return new ResponseEntity<Boolean>(!role.isPresent(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    /**
     * 验证角色名称的唯一性
     * @param request
     * @return
     */
    @PostMapping("/check/roleNo")
    public ResponseEntity<Boolean> checkRoleNo(HttpServletRequest request){
        String roleNo=request.getParameter("value");
        log.info("check role roleNo{}",roleNo);
        Optional<Role> role = roleService.getRoleByRoleNo(roleNo);
        return new ResponseEntity<Boolean>(!role.isPresent(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    @GetMapping("/getMenusByRoleId/{id}")
    public ResponseEntity<List<Menu>> getMenusByRoleId(@PathVariable String id){
        log.debug("REST request to get roles by userId");
        HttpHeaders responseHeaders = HttpResponseHeader.getResponseHeader();
        List<Menu> menus = roleMenuService.getSelectMenus(id);
        return new ResponseEntity<>(menus, responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/saveRoleMenus")
    public ResponseEntity<String> createRole(@RequestParam("roleId") String roleId,@RequestParam("name") String name,
                                             @RequestParam("roleNo") String roleNo,@RequestParam("roleDesc") String roleDesc, @RequestParam("menuIds") String menuIds) throws URISyntaxException {
        log.debug("REST to save menus for every role : {}");
        //判断当前是修改还是保存
        Role role=null;
        if(roleId!=null && !"".equals(roleId)){
             role = roleService.findOne(Long.parseLong(roleId));
             role.setName(name);
             role.setRoleNo(roleNo);
             role.setRoleDesc(roleDesc);
        }else{
            role  =new Role(name,roleNo,roleDesc);
        }
        if ("".equals(menuIds)) {
            role.setMenus(null);
        } else {
            Set<Menu> menus = new HashSet<Menu>();
            Arrays.stream(menuIds.split(",")).forEach(ele ->{
                Menu menu = menuService.findOneByUrl(ele);
                if(menu!=null){
                    menus.add(menu);
                }
            });
            role.setMenus(menus);
        }
        Role result = roleService.saveAndFlush(role);
        return new ResponseEntity<String>("0", HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }


    @GetMapping("/checkRoleHasBindUser/{id}")
    public ResponseEntity<Boolean> checkRoleHasBindUser(@PathVariable Long id){
        log.debug("REST request to check role has bind user by id");
        HttpHeaders responseHeaders = HttpResponseHeader.getResponseHeader();
        Long counts = roleService.getUserByRoleId(id);
        if (counts != null && counts > 0) {
            return new ResponseEntity<Boolean>(true, responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<Boolean>(false, responseHeaders, HttpStatus.OK);
    }
    /*
     * 获取角色的总记录数
     * @return
     */
    @GetMapping("/getAllCountRoles")
    public ResponseEntity<String> getAllCountRoles(){
        return new ResponseEntity<String>(roleService.getAllCountRoles(),HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    @GetMapping("/getAllCountRolesByCondition")
    public long getAllCountRolesByCondition(String roleNo,String roleName){
        log.info("角色模块带参数的查询总记录数{}",roleNo);
        long result =roleService.getAllCountByCondition(roleNo,roleName);
        log.info("返回的结果为{}",result);
        return result;
    }
    @GetMapping("/getCheckMenuUrlByRoleId")
    public ResponseEntity<List<String>> getCheckMenuUrlByRoleId(@RequestParam("roleId") String roleId) {
        List<String> list = new ArrayList<String>();
        Role role = roleService.findOne(Long.parseLong(roleId));
        if (role != null) {
            Set<Menu> menus = role.getMenus();
            if (menus != null && menus.size() > 0) {
                for (Menu menu : menus) {
                    list.add(menu.getUrl());
                }
            }
        }
            return new ResponseEntity<List<String>>(list, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
/*    @PostMapping("/saveNewRoleAndMenus")
    public ResponseEntity<String> saveNewRoleAndMenus(@RequestParam("name") String name,
                                                      @RequestParam("roleNo") String roleNo,@RequestParam("roleDesc") String roleDesc, @RequestParam("menuIds") String menuIds){
        Role role =new Role(name,roleNo,roleDesc);
        Role newRole=roleService.save(role);
        if ("".equals(menuIds)) {
            newRole.setMenus(null);
        } else {
            Set<Menu> menus = new HashSet<Menu>();
            Arrays.stream(menuIds.split(",")).forEach(ele ->{
                Menu menu = menuService.findOneByUrl(ele);
                if(menu!=null){
                    menus.add(menu);
                }
            });
            newRole.setMenus(menus);
        }
        Role result = roleService.saveAndFlush(newRole);
        log.info("{}{}{}{}",name,roleNo,roleDesc);
        return new ResponseEntity<String>("0",HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }*/

    }
