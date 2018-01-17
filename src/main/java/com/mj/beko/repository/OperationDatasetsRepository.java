package com.mj.beko.repository;

import com.mj.beko.domain.OperationDatasets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the OperationDatasets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OperationDatasetsRepository extends JpaRepository<OperationDatasets, Long> {
    @Query(value = "SELECT od.*" +
        " FROM t_operation oper INNER JOIN order_station os" +
        " ON oper.order_station_id = os.id" +
        " INNER JOIN operation_datasets od ON oper.id = od.operation_id" +
        " WHERE os.product_no = ?1 AND os.workstation_id = ?2",nativeQuery = true)
    List<OperationDatasets> getOperationDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId);
}
