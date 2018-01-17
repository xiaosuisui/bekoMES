package com.mj.beko.web.rest;

import com.mj.beko.domain.Operation;
import com.mj.beko.service.OperationService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/24.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class OperationResource {

    private static final String ENTITY_NAME = "operation";

    @Inject
    private OperationService operationService;

    /**
     * 分页查询
     * @param pageUtil
     * @return
     */
    @GetMapping("/operations")
    public ResponseEntity<List<Operation>> queryByPage(PageUtil pageUtil){
        return new ResponseEntity<List<Operation>>(operationService.queryByPage(pageUtil.getPage(), pageUtil.getSize()), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 查询所有的记录数
     * @return
     */
    @GetMapping("/getAllOperationCount")
    public ResponseEntity<String> getAllOperationCount(){
        return new ResponseEntity<String>(operationService.getAllCountOperation(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    @PostMapping("/operations")
    public ResponseEntity<Operation> createOperation(@RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to save Operation : {}", operation);
        if (operation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new operation cannot already have an ID")).body(null);
        }
        Operation result = operationService.save(operation);
        return ResponseEntity.created(new URI("/api/operations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @GetMapping("/operations/{id}")
    public ResponseEntity<Operation> getOperation(@PathVariable Long id) {
        log.debug("REST request to get Operation : {}", id);
        Operation operation = operationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(operation));
    }

    @PutMapping("/operations")
    public ResponseEntity<Operation> updateOperation(@RequestBody Operation operation) throws URISyntaxException {
        log.debug("REST request to update Operation : {}", operation);
        if (operation.getId() == null) {
            return createOperation(operation);
        }
        Operation result = operationService.save(operation);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operation.getId().toString()))
                .body(result);
    }

    @DeleteMapping("/operations/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        log.debug("REST request to delete Operation : {}", id);
        operationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
