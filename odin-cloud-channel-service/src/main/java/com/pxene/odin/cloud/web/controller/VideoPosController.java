package com.pxene.odin.cloud.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.VideoPosVO;
import com.pxene.odin.cloud.service.VideoPosService;
import java.util.List;
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
public class VideoPosController {

  @Autowired
  private VideoPosService videoPosService;

  @GetMapping(value = "/videoPoses",produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaginationResponse> listVideoPos(@RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize) {
    Page<Object> pager = null;
    if (pageNo != null && pageSize != null) {
      pager = PageHelper.startPage(pageNo, pageSize);
    }
    return ResponseEntity.ok(new PaginationResponse(videoPosService.listVideoPoses(), pager));
  }
  @GetMapping(value = "/videoPos/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<VideoPosVO> getVideoPos(@PathVariable(required = false) Integer id) {
    return ResponseEntity.ok(videoPosService.getVideoPosById(id));
  }
}
