package com.pxene.odin.cloud.web.controller;

import java.util.Date;
import java.util.List;

import com.pxene.odin.cloud.domain.vo.SizeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.AdxVO;
import com.pxene.odin.cloud.domain.vo.ContractVO;
import com.pxene.odin.cloud.service.AdxService;

@RestController
public class AdxController {
	
	@Autowired
	AdxService adxService;
	
	/**
	 * 批量查询ADX
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/adxs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAdxs(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询ADX信息
		List<AdxVO> adxs = adxService.listAdxs();
		// 分页
		PaginationResponse result = new PaginationResponse(adxs,pager);
		// 返回ADX信息
		return new ResponseEntity<PaginationResponse>(result,HttpStatus.OK);		
	}
	
	/**
	 * 批量查询定价合同
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/contracts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAdxContracts(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询定价合同信息
		List<ContractVO> contracts = adxService.listAdxContracts();
		// 分页
		PaginationResponse result = new PaginationResponse(contracts,pager);
		// 返回定价合同信息
		return new ResponseEntity<PaginationResponse>(result,HttpStatus.OK);		
	}

	/**
	 * 根据ID查询渠道信息
	 */
	@GetMapping(value = "/adx/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdxVO> getAdx(@PathVariable Integer id) {
		return ResponseEntity.ok(adxService.getAdx(id));
	}
	
	/**
	 * 根据id查询定价合同信息
	 * @param id 定价合同id
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/contract/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContractVO> getContract(@PathVariable Integer id) throws Exception {
		return ResponseEntity.ok(adxService.selectContractById(id));
	}

	/**
	 * 根据合同的开始时间查询定价合同信息
	 * @param todayStart
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/contract/{todayStart}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ContractVO>> selectContractByTodayStart(@PathVariable Date todayStart) throws Exception {
		return ResponseEntity.ok(adxService.selectContractByTodayStart(todayStart));
	}
	
	/**
	 * 根据合同的结束时间查询定价合同信息
	 * @param yesterdayEnd
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/contract/{yesterdayEnd}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ContractVO>> selectContractByYesterdayEnd(@PathVariable Date yesterdayEnd) throws Exception {
		return ResponseEntity.ok(adxService.selectContractByEndStart(yesterdayEnd));
	}
}
