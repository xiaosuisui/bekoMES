package com.mj.beko.domain;

import lombok.Data;

/**
 * Created by Ricardo on 2017/8/18.
 * 扩展一个字段,用来储存用户Id
 */
@Data
public class ProductResourceFileVm extends ProductResourceFile {
//todo。。。。。。。。。。。。。。。。。。。
    //文件
    private String uploadFile;

    //文件上传名称
    private String fileName;

}
