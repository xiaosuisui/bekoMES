package com.mj.beko.service.impl;

import com.mj.beko.domain.Operation;
import com.mj.beko.domain.OperationDatasets;
import com.mj.beko.domain.PartsDatasets;
import com.mj.beko.domain.PartsDatasetsVM;
import com.mj.beko.repository.OperationDatasetsRepository;
import com.mj.beko.repository.OperationRepository;
import com.mj.beko.repository.PartsDatasetsRepository;
import com.mj.beko.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Slf4j
@Service
@Transactional
public class OperationServiceImpl implements OperationService {

    private String localhost = null;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private OperationDatasetsRepository operationDatasetsRepository;

    @Autowired
    private PartsDatasetsRepository partsDatasetsRepository;

    @Override
    public Operation save(Operation operation) {
        log.info("save a operation,{}", operation);
        return operationRepository.save(operation);
    }

    @Override
    public void delete(Operation operation) {

    }

    @Override
    public List<Operation> query() {
        log.info("查询所有的记录");
        return operationRepository.findAll();
    }

    @Override
    public List<Operation> queryByPage(int page, int size) {
        log.info("分页查询operaion{}{}", page, size);
        return operationRepository.queryByPage(page * size, size);
    }

    @Override
    public String getAllCountOperation() {
        log.info("获取总记录数");
        return String.valueOf(operationRepository.count());
    }

    /**
     * 根据productNo和workstationId查找List<Operation>
     */
    @Override
    public List<Operation> getOperationByProductNoAndWorkstationId(String productNo, Long workstationId) {
        return operationRepository.getOperationByProductNoAndWorkstationId(productNo, workstationId);
    }

    @Override
    public Operation findOne(Long id) {
        return operationRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        operationRepository.delete(id);
    }

    /**
     * 根据orderId和workstationId查找List<OperationDatasets>
     */
    @Override
    public List<OperationDatasets> getOperationDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId) {
        return operationDatasetsRepository.getOperationDatasetsByProductNoAndWorkstationId(productNo,workstationId);
    }

    /**
     * 根据productNo和workstationId查找List<PartsDatasets>
     */
    @Override
    public List<PartsDatasets> getPartDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId) {
        return partsDatasetsRepository.getPartDatasetsByProductNoAndWorkstationId(productNo, workstationId);
    }

    /**
     * 根据productNo和workstationId查找只包含picture的List<PartsDatasets>
     */
    @Override
    public List<PartsDatasetsVM> getPicturePartDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId) {
        List<PartsDatasetsVM> pictureList = null;
        List<PartsDatasets> partsDatasetsList = partsDatasetsRepository.getPartDatasetsByProductNoAndWorkstationId(productNo, workstationId);
        if (partsDatasetsList == null) {
            return pictureList;
        }
        try {
            pictureList = new ArrayList<>();
            if (localhost == null) {
                localhost = InetAddress.getLocalHost().getHostAddress();
            }
            for (PartsDatasets partsDatasets : partsDatasetsList
                    ) {
                if (partsDatasets != null && partsDatasets.getPartKey().endsWith("picture")) {
                    PartsDatasetsVM PartsDatasetsVM= new PartsDatasetsVM();
                    PartsDatasetsVM.setId(partsDatasets.getId());
                    PartsDatasetsVM.setPartKey(partsDatasets.getPartKey());
                    PartsDatasetsVM.setPartValue(partsDatasets.getPartValue());
                    PartsDatasetsVM.setConsumedParts(partsDatasets.getConsumedParts());
                    PartsDatasetsVM.setRequestPictureURL("http://" + localhost + "/api/getImage?path=" + partsDatasets.getPartValue());
                    pictureList.add(PartsDatasetsVM);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return pictureList;
    }
}
