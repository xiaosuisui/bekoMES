package com.mj.beko.domain;

import lombok.Data;

/**
 * Created by Ricardo on 2017/8/18.
 * 扩展一个字段,用来储存用户Id
 */
@Data
public class ProductVm extends Product {

    //图片base64
    private String image;

    //图片上传名称
    private String fileName;

}
