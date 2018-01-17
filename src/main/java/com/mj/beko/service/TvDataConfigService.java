package com.mj.beko.service;

import com.mj.beko.domain.TvDataConfig;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/11/17.
 */
public interface TvDataConfigService extends BaseService<TvDataConfig> {
    TvDataConfig findOneById(Long id);

    long getAllTvDataConfigByCondition(String tvName);
    Page<TvDataConfig> findAllTvDataConfigCondition(String tvName,int page, int size);
    void delete(Long id);

}
