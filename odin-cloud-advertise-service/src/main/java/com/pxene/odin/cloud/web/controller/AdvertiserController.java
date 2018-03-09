package com.pxene.odin.cloud.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.AdvertiserVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.service.AdvertiserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 广告主
 * @author lizhuoling
 *
 */
@RestController
@Slf4j
public class AdvertiserController {
	
	@Autowired
	AdvertiserService advertiserService;
	
	/**
	 * 创建广告主
	 * @param advertiser
	 * @return
	 */
	@RequestMapping(value = "/advertiser",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Integer>> createAdvertiser(@Valid @RequestBody AdvertiserVO advertiser) throws Exception{
		log.debug("<=DSP-Advertiser=> Creating Advertiser {}." + advertiser.getName());
		// 判断名称是否重复
		if (advertiserService.isAdvertiserExist(advertiser.getName())) {
			log.debug("<=DSP-Advertiser=> A Advertiser with advertisername already exit." + advertiser.getName());
			throw new DuplicateEntityException(PhrasesConstant.ADVERTISER_NAME_IS_EXIST);			
		}
		// 判断公司名称是否存在
		if(advertiserService.isCompanyNameExist(advertiser.getCompanyName())) {
			log.debug("<=DSP-Advertiser=> A Advertiser with companyname already exit." + advertiser.getCompanyName());
			throw new DuplicateEntityException(PhrasesConstant.COMPANY_NAME_IS_EXIST);
		}
		// 创建广告主
		advertiserService.createAdvertiser(advertiser);
		// 返回广告主id
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("id", advertiser.getId());
		return new ResponseEntity<>(resultMap, HttpStatus.CREATED);		
	}
	
	/**
	 * 编辑广告主
	 * @param id 广告主id
	 * @param advertiser 广告主信息
	 * @return
	 */
	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdvertiserVO> updateAdvertiser(@PathVariable Integer id, @RequestBody AdvertiserVO advertiser) throws Exception {
		log.debug("<=DSP-Advertiser=> Updating advertiser {}." + id);
		// 查询广告主
		AdvertiserVO currentAdvertiser = advertiserService.getAdvertiser(id);
		// 判断广告主是否存在
		if (currentAdvertiser == null) {
			log.debug("<=DSP-Advertiser=> Advertiser with id not found." + id);
			throw new ResourceNotFoundException(PhrasesConstant.ADVERTISER_NOT_FOUND);
		}
		// 判断公司名称是否存在
		if(advertiserService.isCompanyNameAndNotId(advertiser.getCompanyName(), id)) {
			log.debug("<=DSP-Advertiser=> A Advertiser with companyname already exit." + advertiser.getCompanyName());
			throw new DuplicateEntityException(PhrasesConstant.COMPANY_NAME_IS_EXIST);
		}
		advertiserService.updateAdvertiser(id, advertiser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * 根据广告id查询广告主信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/advertiser/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdvertiserVO> getAdvertiser(@PathVariable Integer id) {
		log.info("<=DSP-Advertiser=> Fetching Advertiser with id {}." + id);
		// 根据id查询广告主信息
		AdvertiserVO advertiser = advertiserService.getAdvertiser(id);
		// 判断广告主信息是否为空
		if (advertiser == null) {
			log.debug("<=DSP-Advertiser=> Advertiser with id {} not found." + id);
			throw new ResourceNotFoundException(PhrasesConstant.ADVERTISER_NOT_FOUND);
		}
		return new ResponseEntity<AdvertiserVO>(advertiser, HttpStatus.OK);
	}
	
	/**
	 * 批量查询广告主
	 * @param pageable
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/advertisers",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAdvertisers(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize,
			@RequestParam(required = false) String name, @RequestParam(required = false) String contacts,
			@RequestParam(required = false) String companyName) {
		// 判断分页信息知否为空
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询广告主信息
		List<AdvertiserVO> advertisers = advertiserService.listAdvertisers(name,contacts,companyName);
		// 分页
		PaginationResponse result = new PaginationResponse(advertisers,pager);
		// 返回广告主信息
		return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);
	}
	
	/**
	 * 上传图片
	 * @param file 上传的文件
	 * @param response
	 * @return
	 * @throws Exception
	 */	
	@RequestMapping(value = "/advertiser/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> updateQualification(@RequestPart(value = "file", required = true) MultipartFile file) throws Exception {
		String path = advertiserService.uploadQualification(file);
		// 返回上传url
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("path", path);
		return new ResponseEntity<>(resultMap, HttpStatus.CREATED);		
	}
	
	/**
	 * 上传Logo
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload/logo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> updateLogo(@RequestPart(value = "file", required = true) MultipartFile file) throws Exception {
		String path = advertiserService.uploadLogo(file);
		// 返回上传url
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("path", path);
		return new ResponseEntity<>(resultMap, HttpStatus.CREATED);		
	}
}
