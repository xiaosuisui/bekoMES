package com.mj.beko.util;

import lombok.Data;

/**
 * Created by Ricardo on 2017/8/17.
 * 分页查询
 */
public class PageUtil {
    private int page;
    private int size;
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public PageUtil(int page,int size){
        this.page=page;
        this.size=size;
    }
    public PageUtil(){}
}
