package com.mj.beko.repository;

import com.mj.beko.domain.PartsDatasets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the PartsDatasets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartsDatasetsRepository extends JpaRepository<PartsDatasets, Long> {
@Query(value = "SELECT pd.*" +
    " FROM parts_datasets pd INNER JOIN consumed_parts cp ON cp.id = pd.consumed_parts_id" +
    " INNER JOIN t_operation op ON op.id = cp.operation_id" +
    " INNER JOIN order_station os ON os.id = op.order_station_id" +
    " WHERE os.product_no = ?1 AND os.workstation_id = ?2",nativeQuery = true)
    List<PartsDatasets> getPartDatasetsByProductNoAndWorkstationId(String productNo, Long workstationId);
}
