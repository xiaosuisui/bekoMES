package com.mj.beko.repository;

import com.mj.beko.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/15.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /*通过用户Id查询menuName*/
    @Query(value = "select m.*\n" +
            "         from t_menu m\n" +
            "         left join role_menu rm on m.id = rm.menu_id\n" +
            "         left join t_role r on rm.role_id = r.id\n" +
            "         left join user_role ur on r.id = ur.role_id\n" +
            "         left join t_user u on ur.user_id = u.id\n" +
            "        where u.id=:userId", nativeQuery = true)
    Optional<List<Menu>> findMenuNameByLoginId(@Param("userId") Long userId);

    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_menu ORDER BY id DESC OFFSET :offset ROW FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<Menu> queryByPage(@Param("offset") int offset, @Param("size") int size);

    /**
     * 通过Url查找menu
     * @param url
     * @return
     */
    Menu findOneByUrl(String url);
}
