package com.pxene.odin.cloud.web.controller;

import com.pxene.odin.cloud.domain.model.CampaignKpiModel;
import com.pxene.odin.cloud.domain.model.PolicyModel;
import com.pxene.odin.cloud.domain.vo.CampaignKpiVO;
import com.pxene.odin.cloud.domain.vo.CampaignTargetVO;
import com.pxene.odin.cloud.domain.vo.PolicyKpiVO;
import com.pxene.odin.cloud.domain.vo.PolicyTargetVO;
import com.pxene.odin.cloud.domain.vo.PolicyVO;
import com.pxene.odin.cloud.service.PolicyService;
import com.sun.org.apache.bcel.internal.generic.DUP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.model.CampaignModel;
import com.pxene.odin.cloud.domain.vo.CampaignVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.service.CampaignService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CampaignController {

    @Autowired
    CampaignService campaignService;

    /**
     * 创建活动
     *
     * @param campaignVO 包含活动信息的对象
     * @return HTTP响应码：201,409,响应体id表示新建活动的ID
     */
    @PostMapping(value = "/campaign", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createCampaign(HttpServletRequest request, @Valid @RequestBody CampaignVO campaignVO)
        throws Exception {
        log.info("create Campaign {}.", campaignVO.getName());

        if (campaignService.isNameExist(campaignVO)) {
            throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
        }
        //保存活动校验
        campaignService.checkSaveCampaignRules(campaignVO);
        //活动数据校验
        campaignService.checkCampaignRules(campaignVO);
        
        CampaignModel campaignModel = campaignService.saveCampaign(campaignVO);

        Map<String, Object> map = new HashMap<>();
        map.put("id", campaignModel.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }

    /**
     * 查询活动列表
     *
     * @return HTTP响应码200, 204
     */
    @GetMapping(value = "/campaigns", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> listAllCampaign(
    	@RequestParam(required = false) Integer projectId,@RequestParam(required = false) Long adStartDate, 
    	@RequestParam(required = false) Long adEndDate,@RequestParam(required = false) String advertiserName,
        @RequestParam(required = false) Integer id, @RequestParam(required = false) String name,
        @RequestParam(required = false) String status, @RequestParam(required = false) String auditStatus,
        @RequestParam(required = false) Long startDate, @RequestParam(required = false) Long endDate,
        @RequestParam(required = false) String sortKey, @RequestParam(required = false) String sortType,
        @RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
    	
    	List<CampaignVO> campaigns=null;
    	Page<Object> pager=null;
    	
    	if(status==null){
        	if (pageNo != null && pageSize != null) {
        		pager = PageHelper.startPage(pageNo, pageSize);
        	}
        	campaigns = campaignService
        			.listAllCampaigns(projectId, adStartDate,adEndDate, advertiserName, id, name, status, auditStatus, startDate, endDate, sortKey,
        					sortType);
    	}else{
    		campaigns = campaignService
        			.listAllCampaigns(projectId, adStartDate,adEndDate, advertiserName, id, name, status, auditStatus, startDate, endDate, sortKey,
        					sortType);
    		if (pageNo != null && pageSize != null) {
    			pager = PageHelper.startPage(pageNo, pageSize);
    			pager.setTotal(campaigns.size());
	        	Integer toIndex=pageNo*pageSize;
	        	if(campaigns.size()<toIndex){
	        		toIndex=campaigns.size();
	        	}
	        	campaigns = campaigns.subList((pageNo-1)*pageSize, toIndex);
    		}
    	}

//        if (campaigns == null || campaigns.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        }
        
        PaginationResponse result = new PaginationResponse(campaigns, pager);

        return ResponseEntity.ok(result);

    }

    /**
     * 获取指定ID的活动。
     *
     * @param id 活动ID
     * @return HTTP响应码200，404  响应体中包含查询出的活动实体。
     */
    @GetMapping(value = "/campaign/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignVO> getProject(@PathVariable Integer id) {
        log.info("Fetching campaign with id {}.", id);

        CampaignVO campaign = campaignService.findById(id);

        if (campaign == null) {
            log.debug("Campaign with id {} not found.", id);
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }

        return ResponseEntity.ok(campaign);
    }

    /**
     * 编辑活动
     *
     * @param id 活动ID
     * @param campaignVO 包含活动信息的对象
     * @return HTTP响应码：200,404
     */
    @PutMapping(value = "/campaign/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProject(@PathVariable Integer id, @RequestBody CampaignVO campaignVO) throws Exception {
        log.debug("Updating campaign {}.", id);
        campaignVO.setId(id);
        CampaignVO currentCampaignVO = campaignService.findById(id);

        if (currentCampaignVO == null) {
            log.debug("A Campaign with id {} not found.", id);
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }
        if (!currentCampaignVO.getName().equals(campaignVO.getName())) {
            if (campaignService.isUpdateNameExist(campaignVO)) {
                throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
            }
        }
        //活动数据校验
        campaignService.checkCampaignRules(campaignVO);
        //编辑活动校验
        campaignService.checkUpdateCampaignRules(currentCampaignVO, campaignVO);

        campaignService.updateCampaign(id, currentCampaignVO, campaignVO);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 审核活动
     *
     * @return HTTP响应码：204,404
     */
    @PutMapping(value = "/campaign/audit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> auditCampaign(@PathVariable Integer id, @RequestBody Map<String, String> map) {
        log.debug("Audit campaign {}.", id);

        campaignService.auditCampaign(id, map);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    /**
     * 活动开关状态修改
     *
     * @return HTTP响应码：204,404
     */
    @PutMapping(value = "/campaign/enable/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> enableCampaign(@PathVariable Integer id, @RequestBody Map<String, String> map) throws Exception {
        log.debug("Enable campaign {}.", id);
        String enable = map.get("enable");
        
        //查询活动信息，判断活动是否存在
        CampaignModel campaignModel = campaignService.selectCampaignById(id);
        if (campaignModel == null) {
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }

        if (StatusConstant.OFF_STATUS.equals(enable)
        		||StatusConstant.ON_STATUS.equals(enable)) {
        	campaignModel.setEnable(enable);
            campaignService.enableCampaign(campaignModel);
        } else {
            throw new IllegalArgumentException(PhrasesConstant.PARAM_ERROR);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Autowired
    private PolicyService policyService;

    @PutMapping(value = "/campaign/odinAudit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> odinAudit(@PathVariable Integer id, @RequestBody(required = true) Map<String, Object> map)
        throws Exception {
        if (!map.containsKey("auditStatus") || !map.containsKey("realBid")) {
            throw new IllegalArgumentException(PhrasesConstant.LACK_NECESSARY_PARAM);
        }
        String[] statusArr = {StatusConstant.AUDIT_CAMPAIGN_WAIT_FOR_AUDIT, StatusConstant.AUDIT_CAMPAIGN_AUDIT_APPROVED};
        if (org.apache.commons.lang3.StringUtils.isBlank(map.get("auditStatus").toString()) || !org.apache.commons.lang3.StringUtils
            .isNumeric(map.get("realBid").toString()) || !Arrays.asList(statusArr).contains(map.get("auditStatus").toString())) {
            throw new IllegalArgumentException(PhrasesConstant.PARAM_ERROR);
        }
        Integer flag = campaignService.updateauditStatus(map.get("auditStatus").toString(), id);
        if (flag == 0) {
            throw new DuplicateEntityException(PhrasesConstant.AUDIT_STATUS_FAILED);
        }
        CampaignVO campaignModel = campaignService.findById(id);
        if (campaignModel == null) {
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }
        PolicyVO policyVO = campaignService.convertVO(campaignModel, map.get("realBid").toString());
        policyService.savePolicy(policyVO);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
