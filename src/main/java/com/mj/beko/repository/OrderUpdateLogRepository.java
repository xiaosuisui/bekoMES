package com.mj.beko.repository;

import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.domain.Pallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/11/9.
 */
@Repository
public interface OrderUpdateLogRepository extends JpaRepository<OrderUpdateLog,Long>,JpaSpecificationExecutor {


}
