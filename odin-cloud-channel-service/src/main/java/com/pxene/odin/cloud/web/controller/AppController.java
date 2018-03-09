package com.pxene.odin.cloud.web.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.PhrasesConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.AppTypeVO;
import com.pxene.odin.cloud.domain.vo.AppVO;
import com.pxene.odin.cloud.service.AppService;

@RestController
public class AppController {
	
	@Autowired
	private AppService appService;
	
	/**
	 * 批量查询媒体类型
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/appTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAppTypes(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询媒体类型信息
		List<AppTypeVO> appTypes = appService.listAppTypes();
		// 分页
		PaginationResponse result = new PaginationResponse(appTypes,pager);
		// 返回信息
		return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);		
	}
	
	/**
	 * 批量查询媒体
	 * @param adxId 广告平台ID
	 * @param name 	媒体名称
	 * @param searchType 搜索类型
	 * @param pageable 分页信息
	 * @return
	 */
	@RequestMapping(value = "/apps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listApps(@Valid @RequestBody AppVO appVO, @RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo,pageSize);
		}
		// 根据id查询媒体信息 
		// 查询媒体信息
		Integer adxId = appVO.getAdxId();
		if (adxId == null) {
			throw new IllegalArgumentException(PhrasesConstant.LACK_NECESSARY_PARAM);
		}
		String[] names = appVO.getNames();
		String searchType = appVO.getSearchType();
		List<AppVO> apps = appService.listApps(adxId, names, searchType);
		// 分页
		PaginationResponse result = new PaginationResponse(apps, pager);
		// 返回信息
		return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);
		
	}
	
	/**
	 * 根据ID批量查询媒体
	 * @param ids：Adx Id和App Id
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/apps/ids", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAppsByIds(@Valid @RequestBody Map<String,List<String>> map, @RequestParam(required = false)Integer pageNo,
			@RequestParam(required = false)Integer pageSize) {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 根据id查询媒体信息 
		List<String> idList = map.get("ids");
		String[] ids = (String[])idList.toArray(new String[idList.size()]);
		List<AppVO> apps = appService.listAppsByIds(ids);
		// 分页
		PaginationResponse result = new PaginationResponse(apps, pager);
		// 返回信息
		return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);
	}
}
