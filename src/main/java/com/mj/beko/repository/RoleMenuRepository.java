package com.mj.beko.repository;

import com.mj.beko.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * Created by xiaosui on 2017/6/29.
 */
public interface RoleMenuRepository extends JpaRepository<Menu, Long> {

    @Query(value = "select m.* from t_menu m where exists (select 1 from role_menu rm WHERE m.id=rm.menu_id and rm.role_id=?1)", nativeQuery = true)
    List<Menu> getSelectMenus(String roleId);

}
