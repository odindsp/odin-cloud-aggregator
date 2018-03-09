package com.pxene.odin.cloud.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.RegionVO;
import com.pxene.odin.cloud.service.RegionService;

@RestController
public class RegionController {
	
	@Autowired 
	RegionService regionService;
	
	@RequestMapping(value = "/regions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listRegions(@RequestParam(required = false)Integer pageNo, @RequestParam(required = false)Integer pageSize) {
		// 判断分页信息是否为空 
		Page<Object> pager = null;
		if (pageNo !=null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询地域信息
		List<RegionVO> regions = regionService.listRegions();
		// 分页
		PaginationResponse result = new PaginationResponse(regions,pager);
		// 返回信息
		return new ResponseEntity<PaginationResponse>(result,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/region", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<RegionVO> selectRegions() {
		// 查询地域信息
		List<RegionVO> regions = regionService.selectRegions();
		// 返回信息
		return regions;
	}
}
