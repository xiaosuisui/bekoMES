package com.mj.beko.service;

import com.mj.beko.domain.Workstation;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

/**
 * Created by jc on 2017/8/23.
 */
public interface WorkstationService extends BaseService<Workstation> {

    /**
     * 查询所有的工位名称
     * @return
     */
    String getAllCountWorkstaion();

    /**
     * 根据stationId查询工位信息
     * @return
     */
    Workstation getWorkstationByStationId(String stationId);

    /**
     * 根据stationName查询工位信息
     * @param stationName
     * @return
     */
    Workstation getWorkstationByStationName(String stationName);

    Workstation findOne(Long id);

    void delete(Long id);

    Optional<Workstation> updateWorkstation(Workstation workstation);

    List<Workstation> findAll();

    /**
     * 分页条件查询所有的数据
     * @param stationId
     * @param stationName
     * @param page
     * @param size
     * @return
     */
    Page<Workstation> findAllByPageAndCondition(String stationId, String stationName, int page, int size);

    /**
     * 查询条件下的总记录数
     * @param stationId
     * @param stationName
     * @return
     */
    long getAllCountByCondition(String stationId,String stationName);
}
