package com.mj.beko.repository;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.FailureReason;
import com.mj.beko.domain.FailureReasonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/11/14.
 */
@Repository
public interface FailureReasonRepository extends JpaRepository<FailureReason,Long>,JpaSpecificationExecutor {
}
