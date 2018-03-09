package com.pxene.odin.cloud.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.BrandVO;
import com.pxene.odin.cloud.service.BrandService;

@RestController
public class BrandController {
	
	@Autowired
	BrandService brandService;
	
	/**
	 * 批量查询手机品牌
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/brands", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listBrands(@RequestParam(required = false)Integer pageNo, @RequestParam(required = false)Integer pageSize) throws Exception {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询手机品牌信息
		List<BrandVO> brands = brandService.listBrands();
		// 分页
		PaginationResponse result = new PaginationResponse(brands, pager);
		return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);
	}
}
