package com.pxene.odin.cloud.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.model.PolicyModel;
import com.pxene.odin.cloud.domain.vo.CampaignVO;
import com.pxene.odin.cloud.domain.vo.PolicyVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.service.CampaignService;
import com.pxene.odin.cloud.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class PolicyController {

	@Autowired
	PolicyService policyService;
	@Autowired
	CampaignService campaignService;

	/**
	 * 创建策略
	 *
	 * @param request
	 * @param policyVO
	 * @return HTTP响应码：201,409,响应体id表示新建策略的ID
	 * @throws Exception 
	 */

	@PostMapping(value = "/policy", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> createPolicy(HttpServletRequest request,
			@Valid @RequestBody PolicyVO policyVO) throws Exception {
		log.info("create Policy {}.", policyVO.getName());

		if (policyService.isNameExist(policyVO)) {
			throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
		}

		PolicyModel policyModel = policyService.savePolicy(policyVO);

		Map<String, Object> map = new HashMap<>();
		map.put("id", policyModel.getId());

		return ResponseEntity.status(HttpStatus.CREATED).body(map);
	}

	/**
	 * 获取指定ID的策略。
	 *
	 * @param id 策略ID
	 *
	 *
	 * @return HTTP响应码200，404 响应体中包含查询出的策略实体。
	 */
	@GetMapping(value = "/policy/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyVO> getProject(@PathVariable Integer id) {
		log.info("Fetching policy with id {}.", id);

		PolicyVO policy = policyService.findById(id);

		if (policy == null) {
			log.debug("policy with id {} not found.", id);
			throw new ResourceNotFoundException(PhrasesConstant.POLICY_NOT_FOUND);
		}

		return ResponseEntity.ok(policy);
	}

	/**
	 * 编辑策略
	 *
	 * @param id 策略ID
	 *
	 *
	 * @param policyVO 包含策略信息的对象
	 *
	 *
	 * @return HTTP响应码：200,404
	 * @throws Exception 
	 */
	@PutMapping(value = "/policy/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updatePolicy(@PathVariable Integer id, @RequestBody PolicyVO policyVO) throws Exception {
		log.debug("Updating policy {}.", id);
		policyVO.setId(id);
		PolicyVO currentPolicyVO = policyService.findById(id);

		if (currentPolicyVO == null) {
			log.debug("A policy with id {} not found.", id);
			throw new ResourceNotFoundException(PhrasesConstant.POLICY_NOT_FOUND);
		}

		if(!currentPolicyVO.getName().equals(policyVO.getName())){
			if(policyService.isUpdateNameExist(policyVO)){
				throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
			}
		}

		CampaignVO campaignVO = campaignService.findById(currentPolicyVO.getCampaignId());
		if (campaignVO != null) {
			policyService.checkPolicyRules(campaignVO, policyVO);

			policyService.checkUpdatePolicyRules(currentPolicyVO,policyVO);
		}

		policyService.updatePolicy(id,currentPolicyVO,policyVO);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	/**
	 * 查询策略列表
	 * @param campaignId
	 * @param advertiseDate
	 * @param id
	 * @param name
	 * @param status
	 * @param startDate
	 * @param endDate
	 * @param sortKey
	 * @param sortType
	 * @param pageNo
	 * @param pageSize
	 * @return HTTP响应码200,204
	 */
	@GetMapping(value = "/policys", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAllPolicy(@RequestParam(required = false) Integer campaignId,@RequestParam(required = false) Long adStartDate,
			@RequestParam(required = false) Long adEndDate, @RequestParam(required = false) Integer id,
			@RequestParam(required = false) String name, @RequestParam(required = false) String status,
			@RequestParam(required = false) Long startDate, @RequestParam(required = false) Long endDate,
			@RequestParam(required = false) String sortKey, @RequestParam(required = false) String sortType,
			@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
		
		List<PolicyVO> policys=null;
    	Page<Object> pager=null;
		
		if(status==null){
			if (pageNo != null && pageSize != null) {
				pager = PageHelper.startPage(pageNo, pageSize);
			}
			policys = policyService.listAllPolicys(campaignId, adStartDate,adEndDate, id,
					name, status, startDate, endDate, sortKey, sortType);
		}else{
			policys = policyService.listAllPolicys(campaignId, adStartDate,adEndDate, id,
					name, status, startDate, endDate, sortKey, sortType);
			if (pageNo != null && pageSize != null) {
				pager = PageHelper.startPage(pageNo, pageSize);
				pager.setTotal(policys.size());
				Integer toIndex=pageNo*pageSize;
	        	if(policys.size()<toIndex){
	        		toIndex=policys.size();
	        	}
	        	policys = policys.subList((pageNo-1)*pageSize, toIndex);
			}
		}
		
//		if (policys == null || policys.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//		}

		PaginationResponse result = new PaginationResponse(policys, pager);

		return ResponseEntity.ok(result);

	}

    /**
     * 策略下创意开关
     * @param id
     * @return
     */
    @PutMapping(value = "/policy/creative/enable/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> enableCreative(@RequestBody Map<String, String> map,
        @PathVariable Integer id) {
        String[] arr = {StatusConstant.ON_STATUS, StatusConstant.OFF_STATUS};
        if (!map.containsKey("enable") || !Arrays.asList(arr).contains(map.get("enable").toString())) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
        }
        policyService.enableCreative(map.get("enable").toString(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

	/**
	 * 策略下导入创意
	 * @param id
	 * @param map
     * @return
     */
	@PutMapping(value = "/policy/creative/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> policyAddCreative(@PathVariable Integer id, @RequestBody Map<String,Object> map) throws Exception {
		log.debug("policy add creative {}.", id);

		policyService.policyAddCreative(id, map);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	/**
	 * 策略下导入物料包下创意
	 * @param id
	 * @param map
     * @return
     */
	@PutMapping(value = "/policy/package/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> policyAddPackageCreative(@PathVariable Integer id, @RequestBody Map<String,Object> map) throws Exception {
		log.debug("policy add package creative {}.", id);

		policyService.policyAddPackageCreative(id, map);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	/**
     * 策略开关状态修改
     *
     * @return HTTP响应码：204,404
     */
    @PutMapping(value = "/policy/enable/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> enablePolicy(@PathVariable Integer id, @RequestBody Map<String, String> map) throws Exception {
        log.debug("Enable policy {}.", id);
        String enable = map.get("enable");
       
        //查询策略信息，判断策略是否存在
        PolicyModel policyModel = policyService.selectPolicyById(id);
        if (policyModel == null) {
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }

        if (StatusConstant.OFF_STATUS.equals(enable)
        		|| StatusConstant.ON_STATUS.equals(enable)) {
        	policyModel.setEnable(enable);
            policyService.enablePolicy(policyModel);
        } else {
            throw new IllegalArgumentException(PhrasesConstant.PARAM_ERROR);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
    
    @PostMapping(value="/policy/auto", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> autoCreatePolicy(@RequestBody Map<String, Integer> map) throws Exception{
    	log.info("auto Create Policy,the campaignId is {}." ,map.get("campaignId"));
    	Integer campaignId = map.get("campaignId");
    	Integer bid = map.get("bid");
    	
    	if(campaignId==null || bid ==null){
    		throw new IllegalArgumentException();
    	}
    	
    	PolicyModel policyModel=policyService.autoCreatePolicy(campaignId,bid);
    	
    	Map<String, Object> result=new HashMap<>();
    	result.put("id", policyModel.getId());
    	return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @PutMapping(value = "/policy/realBid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePolicyBid(@PathVariable Integer id, @RequestBody Map<String, Integer> map) throws Exception {
        log.debug("update policy {} bid.", id);
        Integer realBid = map.get("bid");
        
        if(realBid < 0){
        	throw new DuplicateEntityException(PhrasesConstant.POLICY_BID_ERROR);
        }
       
        //查询策略信息，判断策略是否存在
        PolicyVO policyVO = policyService.findById(id);
        if (policyVO == null) {
            throw new ResourceNotFoundException(PhrasesConstant.POLICY_NOT_FOUND);
        }
        
        policyVO.setRealBid(realBid);
        policyService.updatePolicyBid(policyVO);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
