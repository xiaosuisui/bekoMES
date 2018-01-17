package com.mj.beko.repository;

import com.mj.beko.domain.TestStationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/11.
 */
@Repository
public interface TestStationDataRespository extends JpaRepository<TestStationData, Long>,JpaSpecificationExecutor {

    /**
     * 根据下底盘条码获取最近一次的气密检测结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    @Query(value = "select count(0) " +
                     "from (" +
                             "select top 3 result " +
                               "from test_station_data " +
                              "where bar_code = ?1 " +
                                "and content_type like 'LeakageTest%' " +
                           "order by create_time desc) tb " +
                    "where tb.result = 'NOK'",
            nativeQuery = true)
    int getAirtightNokCountByBottomPlaceCode(String bottomPlaceCode);

    /**
     * 根据下底盘条码获取最近一次的流量检测结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    @Query(value = "select count(0) " +
                     "from (" +
                             "select top 4 result " +
                               "from test_station_data " +
                              "where bar_code = ?1 " +
                                "and content_type like 'FlowTest%' " +
                           "order by create_time desc) tb " +
                    "where tb.result = 'NOK'",
            nativeQuery = true)
    int getFluxNokCountByBottomPlaceCode(String bottomPlaceCode);

    /**
     * 根据下底盘条码获取打螺丝工位结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    @Query(value = "select count(0) from test_station_data where bar_code = ?1 and content_type like 'Screw%' and result = 'NOK'", nativeQuery = true)
    int getScrewsNokCountByBottomPlaceCode(String bottomPlaceCode);

    @Query(value = "select * from test_station_data where bar_code=:barCode and step=:step and value='OK'",nativeQuery = true)
    List<TestStationData> getDiffTestStationResultMark(@Param("barCode") String barCode, @Param("step") String step);
}
