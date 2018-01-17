package com.mj.beko.service;

import com.mj.beko.domain.Operation;
import com.mj.beko.domain.OperationDatasets;
import com.mj.beko.domain.PartsDatasets;
import com.mj.beko.domain.PartsDatasetsVM;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface OperationService extends BaseService<Operation> {
    /**
     * 获取总记录数
     * @return
     */
    String getAllCountOperation();

    /**
     * 根据productNo和workstationId查找List<Operation>
     */
    List<Operation> getOperationByProductNoAndWorkstationId(String productNo, Long workstationId);

    Operation findOne(Long id);

    void delete(Long id);

    /**
     * 根据orderId和workstationId查找List<OperationDatasets>
     */
    List<OperationDatasets> getOperationDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId);

    /**
     * 根据productNo和workstationId查找List<PartsDatasets>
     */
    List<PartsDatasets> getPartDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId);

    /**
     * 根据productNo和workstationId查找只包含picture的List<PartsDatasets>
     */
    List<PartsDatasetsVM> getPicturePartDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId);
}
