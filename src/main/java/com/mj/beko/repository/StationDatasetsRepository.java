package com.mj.beko.repository;

import com.mj.beko.domain.StationDatasets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StationDatasets entity.
 */
@Repository
public interface StationDatasetsRepository extends JpaRepository<StationDatasets, Long> {

}
