package com.mj.beko.service;

import com.mj.beko.domain.Menu;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/17.
 */
public interface MenuService extends BaseService<Menu>{

    //获取当前用户对应得权限
    List<String> getUserAccessUrls();

    Menu findOne(Long id);
    Menu findOneByUrl(String url);

}
