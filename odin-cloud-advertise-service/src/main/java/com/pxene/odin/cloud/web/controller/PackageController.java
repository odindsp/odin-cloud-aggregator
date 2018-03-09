package com.pxene.odin.cloud.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.model.CreativeModel;
import com.pxene.odin.cloud.domain.vo.PackageVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.service.CreativeService;
import com.pxene.odin.cloud.service.PackageService;

import lombok.extern.slf4j.Slf4j;

/**
 * 物料包
 * @author lizhuoling
 *
 */
@RestController
@Slf4j
public class PackageController {

	@Autowired
	PackageService packageService;

	@Autowired
	private CreativeService creativeService;

	/**
	 * 创建物料包
	 * @param packageVO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/package", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Integer>> createPackage(@Valid @RequestBody PackageVO packageVO) throws Exception {

		log.debug("<=DSP-Advertiser=> Creating Package {}." + packageVO.getName());
		// 判断名称是否重复
		String name = packageVO.getName();
		Integer campaignId = packageVO.getCampaignId();
		if (packageService.isPackageNameExist(name, campaignId)) {
			log.debug("<=DSP-Advertiser=> PackageName already exit." + packageVO.getName());
			throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
		}
		// 创建物料包
		packageService.createPackage(packageVO);
		// 返回物料包ID
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("id", packageVO.getId());
		return new ResponseEntity<>(resultMap,HttpStatus.CREATED);
	}

	/**
	 * 编辑物料包
	 * @param id 物料包ID
	 * @param packageVO 物料包信息
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/package/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PackageVO> updatePackage(@PathVariable Integer id, @RequestBody PackageVO packageVO) throws Exception {
		log.debug("<=DSP-Advertiser=> Updating package {}." + id);
		// 查询物料包
		PackageVO packageDB = packageService.getPackage(id);
		// 判断物料包是否存在
		if (packageDB == null) {
			log.debug("<=DSP-Advertiser=> Package with id not found." + id);
			throw new DuplicateEntityException(PhrasesConstant.PACKAGE_IS_NOT_EXIST);
		}
		// 判断物料包名称是否重复
		if (packageService.isUpdateNameExist(id, packageVO)) {
			log.debug("<=DSP-Advertiser=> PackageName already exit." + packageVO.getName());
			throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
		}
		// 更新物料包
		packageService.updatePackage(id, packageVO);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * 根据物料包id查询物料包详情
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/package/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PackageVO> getPackage(@PathVariable Integer id) throws Exception {
		log.info("<=DSP-Advertiser=> Fetching Package with id {}." + id);
		// 查询物料包信息
		PackageVO packageVO = packageService.getPackage(id);
		// 判断物料包信息是否为空
		if (packageVO == null) {
			log.debug("<=DSP-Advertiser=> Package with id not found." + id);
			throw new DuplicateEntityException(PhrasesConstant.PACKAGE_IS_NOT_EXIST);
		}
		return new ResponseEntity<PackageVO>(packageVO, HttpStatus.OK);
	}

	/**
	 * 批量查询物料包
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/packages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listPackages(@RequestParam(required = false)Integer campaignId, @RequestParam(required = false)Integer pageNo,
			@RequestParam(required = false)Integer pageSize) throws Exception {
		// 分页信息
		Page<Object> pager = null;
		if (pageNo !=null && pageSize != null) {
			pager = PageHelper.startPage(pageNo, pageSize);
		}
		// 查询物料包信息
		List<PackageVO> packages = packageService.listPackages(campaignId);
		// 分页
		PaginationResponse result = new PaginationResponse(packages, pager);
		// 返回物料包信息
		return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);
	}

	/**
	 * 编辑创意名称
	 * @param id   创意ID
	 * @param map  包含创意名称的请求参数，格式为JSON，如{"name": "tony"}
	 * @return
	 * @throws Exception
	 */
	@PutMapping(value = "/package/creative/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateCreativeName(@PathVariable(name = "id", required = true) Integer id, @RequestBody Map<String, String> map) throws Exception
	{
	    CreativeModel creative = creativeService.getCreativeById(id);
	    if (creative == null)
	    {
	        throw new ResourceNotFoundException();
	    }

	    String name = map.get("name");
	    if (StringUtils.isEmpty(name))
	    {
	        throw new IllegalArgumentException();
	    }

	    if (name.length() > 20)
	    {
	        throw new IllegalArgumentException(PhrasesConstant.LENGTH_ERROR_NAME);
	    }

	    creativeService.updateCreativeNameById(id, name);

	    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

    @GetMapping(value = "/package/checkCode/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> checkCode(@PathVariable Integer id){
        Map<String,String> map = new HashMap();
        map.put("result", packageService.checkCode(id));
        return ResponseEntity.ok(map);
    }
}
