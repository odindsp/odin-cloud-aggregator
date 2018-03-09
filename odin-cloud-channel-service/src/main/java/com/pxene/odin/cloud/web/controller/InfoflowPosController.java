package com.pxene.odin.cloud.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.InfoflowVO;
import com.pxene.odin.cloud.service.InfoflowPosService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangshiyi
 */
@RestController
public class InfoflowPosController {

    @Autowired
    private InfoflowPosService infoflowTmplService;

    @GetMapping(value = "/infoflowTmpls", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> listInfoFlowTmpl(
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize) {
        Page<Object> pager = null;
        if (pageNo != null && pageSize != null) {
            pager = PageHelper.startPage(pageNo, pageSize);
        }
        return ResponseEntity.ok(new PaginationResponse(infoflowTmplService.listInfoflowTmpl(), pager));
    }

    @GetMapping(value = "/infoflow/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoflowVO> getinfoflowPos(@PathVariable Integer id) {
        return ResponseEntity.ok(infoflowTmplService.getInfoflow(id));
    }

    @GetMapping(value = "/infoflowPoses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> listInfoflowPos(@RequestParam(required = false) Integer pageNo,
                                                              @RequestParam(required = false) Integer pageSize) {
        Page<Object> pager = null;
        if (pageNo != null && pageSize != null) {
            pager = PageHelper.startPage(pageNo, pageSize);
        }
        return ResponseEntity.ok(new PaginationResponse(infoflowTmplService.listInfoflowPos(), pager));
    }
}
