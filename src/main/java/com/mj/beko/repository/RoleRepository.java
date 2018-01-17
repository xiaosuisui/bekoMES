package com.mj.beko.repository;

import com.mj.beko.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/18.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,JpaSpecificationExecutor {

    @Query(value = "select r.* from t_role r where exists (select 1 from user_role ru where r.id=ru.role_id and ru.user_id=?1)", nativeQuery = true)
    List<Role> findRolesByUsers(Long userId);

    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_role ORDER BY id ASC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<Role> queryByPage(@Param("offset") int offset, @Param("size") int size);

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

    @Query(value = "select count(0) from t_user u where exists (select 1 from user_role ur where u.id = ur.user_id and ur.role_id = ?1)", nativeQuery = true)
    Long getUserByRoleId(Long id);
}
