package com.mj.beko.repository;

import com.mj.beko.domain.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Created by jc on 2017/8/23.
 */
@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, Long>,JpaSpecificationExecutor{

    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_workstation ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<Workstation> queryByPage(@Param("offset") int offset, @Param("size") int size);

    Workstation getWorkstationByStationId(String stationId);

    Workstation getWorkstationByStationName(String stationName);
}
