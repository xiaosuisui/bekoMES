package com.mj.beko.repository;

import com.mj.beko.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/15.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> ,JpaSpecificationExecutor{

    /*通过用户名查询用户*/
    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByEmail(String email);

    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_user ORDER BY id DESC OFFSET :offset ROW FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<User> queryByPage(@Param("offset") int offset, @Param("size") int size);
}
