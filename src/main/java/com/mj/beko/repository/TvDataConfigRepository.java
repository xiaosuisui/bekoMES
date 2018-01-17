package com.mj.beko.repository;

import com.mj.beko.domain.TcsOrder;
import com.mj.beko.domain.TvDataConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/11/17.
 * 电视信息的配置参数（包含图片的显示）
 */
@Repository
public interface TvDataConfigRepository extends JpaRepository<TvDataConfig, Long> ,JpaSpecificationExecutor {

}
