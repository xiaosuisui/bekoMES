package com.mj.beko.web.rest;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.Pallet;
import com.mj.beko.repository.PalletRepository;
import com.mj.beko.service.PalletService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/8/23.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class PalletResource {
    @Autowired
    private PalletService palletService;
    @Autowired
    private PalletRepository palletRepository;
    /**
     * 查询总记录数
     * @return
     */
    @GetMapping("/getCountPallet")
    public ResponseEntity<String> getCountPallet(){
        log.info("查询总记录数");
        return new ResponseEntity<String>(palletService.getAllCountPallet(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    @PostMapping("/pallets")
    public ResponseEntity<Pallet> createPallet(@RequestBody Pallet pallet) throws URISyntaxException {
       log.info("添加一条托盘记录");
        if (pallet.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pallet cannot already have an ID")).body(null);
        } else if (palletRepository.findOneByPalletNo(pallet.getPalletNo()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "userexists", " palletNo already in use")).body(null);
        }
        Pallet result = palletService.save(pallet);
        return ResponseEntity.created(new URI("/api/pallets/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/pallets/{id}")
    public ResponseEntity<Pallet> getOrder(@PathVariable Long id) {
        log.debug("REST request to get pallet : {}", id);
        Pallet pallet = palletService.getPallet(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(pallet));
    }

    @PutMapping("/pallets")
    public ResponseEntity<Pallet> updataPallets(@RequestBody Pallet pallet) throws URISyntaxException {
        log.debug("REST request to update Pallet : {}", pallet);
        if (pallet.getId() == null) {
            return createPallet(pallet);
        }
        Pallet result = palletService.save(pallet);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, pallet.getId().toString()))
                .body(result);
    }

    @DeleteMapping("/pallets/{id}")
    public ResponseEntity<Void> deletePallets(@PathVariable Long id){
        palletService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllPalletsByCondition")
    public long getCountByCondition(String palletName,String palletNo){
        log.info("订单模块带参数的查询总记录数{}",palletNo);
        long result =palletService.getAllCountByCondition(palletName,palletNo);
        log.info("返回的结果为{}",result);
        return result;
    }
    //带参数的查询分页
    @GetMapping("/pallets")
    public ResponseEntity<List<Pallet>> orderByPage(int page, int size, String palletName, String palletNo){
        log.info("订单带参数的分页查询,{}{}",page,palletNo);
        Page<Pallet> palletPage =palletService.findAllByPageAndCondition(palletNo,palletName,page,size);
        List<Pallet> pallets=palletPage.getContent();
        return new ResponseEntity<List<Pallet>>(pallets,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }

}
