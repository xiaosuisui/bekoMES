package com.mj.beko.service;

import com.mj.beko.util.PageUtil;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/17.
 * 封装增删改
 */
public interface BaseService<T> {
    /**
     * 保存
     * @param t
     * @return
     */
    T save(T t);

    /**
     * 删除
     * @param t
     */
    void delete(T t);

    /**
     * 查询
     * @return
     */
    List<T> query();

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    List<T> queryByPage(int page,int size);

}
