package com.pxene.odin.cloud.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.SizeVO;
import com.pxene.odin.cloud.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SizeController {

  @Autowired
  private SizeService sizeService;

  /**
   * 批量查询图片尺寸
   */
  @GetMapping(value = "/sizes", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaginationResponse> listSizes(
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize) {
    Page<Object> pager = null;
    if (pageNo != null && pageSize != null) {
      pager = PageHelper.startPage(pageNo, pageSize);
    }
    return ResponseEntity.ok(new PaginationResponse(sizeService.listSizes(), pager));
  }

  /**
   * 根据ID查询图片尺寸
   */
  @GetMapping(value = "/size/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SizeVO> getSize(@PathVariable Integer id) {
    return ResponseEntity.ok(sizeService.getSize(id));
  }
}
