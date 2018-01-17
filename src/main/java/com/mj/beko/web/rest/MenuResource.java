package com.mj.beko.web.rest;

import com.mj.beko.domain.Menu;
import com.mj.beko.service.MenuService;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MenuResource {

    @Autowired
    private MenuService menuService;

    @GetMapping("/menus")
    public ResponseEntity<List<Menu>> getAllMenus(PageUtil pageUtil) {
        log.debug("REST request to get a page of Menus");
        return new ResponseEntity<>(menuService.queryByPage(pageUtil.getPage(), pageUtil.getSize()), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
}
