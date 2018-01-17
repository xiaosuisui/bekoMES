package com.mj.beko.repository;

import com.mj.beko.domain.ConsumedParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the ConsumedParts entity.
 */
@Repository
public interface ConsumedPartsRepository extends JpaRepository<ConsumedParts, Long> {

    /**
     * 根据产品类型和工位获取ComsumerPart
     * @param productNo
     * @param stationName
     * @return
     */
    @Query(value = "select * " +
                     "from consumed_parts a " +
                "left join t_operation b on a.operation_id = b.id " +
                "left join order_station c on b.order_station_id = c.id " +
                "left join t_workstation d on c.workstation_id = d.id " +
                    "where c.product_no = ?1 " +
                      "and d.station_name = ?2", nativeQuery = true)
    List<ConsumedParts> getConsumPartsByProducntNoAndStation(String productNo, String stationName);
}
