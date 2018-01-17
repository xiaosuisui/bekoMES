package com.mj.beko.repository;

import com.mj.beko.domain.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_operation ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<Operation> queryByPage(@Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT oper.*" +
            " FROM t_operation oper INNER JOIN order_station os" +
            " ON oper.order_station_id = os.id WHERE os.product_no = ?1 AND os.workstation_id = ?2",nativeQuery = true)
    List<Operation> getOperationByProductNoAndWorkstationId(String productNo, Long workstationId);
}
