package com.mj.beko.repository;

import com.mj.beko.domain.OrderStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderStation entity.
 */
@Repository
public interface OrderStationRepository extends JpaRepository<OrderStation, Long> {

    OrderStation findOrderStationByProductNo(String productNo);
}
