package com.mj.beko.repository;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.PalletTypeAndCapility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/12/20.
 */
@Repository
public interface PalletTypeAndCapilityRepository extends JpaRepository<PalletTypeAndCapility,Long>,JpaSpecificationExecutor {
    /**
     * 工位查询对应的托盘的标准的产量
     * @param workStation
     * @return
     */
    @Query(value = "select top 1 * from pallet_type_and_capility where work_station=:workStation",nativeQuery = true)
    PalletTypeAndCapility getPalletTypeAndCapilitiesByWorkStation(@Param("workStation") String workStation);
}
