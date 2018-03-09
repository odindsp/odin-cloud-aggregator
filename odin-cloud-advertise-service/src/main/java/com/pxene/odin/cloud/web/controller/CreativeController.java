package com.pxene.odin.cloud.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.vo.CreativeVO;
import com.pxene.odin.cloud.domain.vo.ImageVo;
import com.pxene.odin.cloud.domain.vo.OdinAuditVO;
import com.pxene.odin.cloud.domain.vo.VideoVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.service.CreativeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/9/19. 创意
 */
@RestController
@Slf4j
public class CreativeController {

    @Autowired
    CreativeService creativeService;

    /**
     * 创建创意
     */
    @RequestMapping(value = "/package/creative", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Integer>>> createCreative(@Valid @RequestBody CreativeVO creative) throws Exception {
        log.info("create Creative {}." + creative.getName());
        List<Integer> ids = creativeService.saveCreative(creative);
        Map<String, List<Integer>> resultMap = new HashMap<>();
        resultMap.put("ids", ids);
        return new ResponseEntity<>(resultMap, HttpStatus.CREATED);
    }

    /**
     * 批量查询创意
     *
     * @param campaignId 活动id
     * @param packageId 物料包id
     * @param policyId 投放策略id
     * @param creativeType 创意类型
     * @param auditStatus 奥丁审核状态
     */
    @RequestMapping(value = "/creatives", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> listCreatives(@RequestParam(required = false) Integer campaignId,
        @RequestParam(required = false) Integer packageId, @RequestParam(required = false) Integer policyId,
        @RequestParam(required = false) String creativeType, @RequestParam(required = false) String auditStatus,
        @RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize) {
        Page<Object> pager = null;
        if (pageNo != null && pageSize != null) {
            pager = PageHelper.startPage(pageNo, pageSize);
        }

        List<CreativeVO> creatives = creativeService.listCreatives(campaignId, packageId, policyId, creativeType, auditStatus);

        // 列表查询出结果为空的情况，返回code也为200 ------(add by zhengyi at 20170927)
//        if (creatives == null || creatives.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        }

        PaginationResponse result = new PaginationResponse(creatives, pager);

        return ResponseEntity.ok(result);
    }

    /**
     * 物料下创意开关
     */
    @PutMapping(value = "/package/creative/enable/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> enableCreative(@RequestBody Map<String, String> map, @PathVariable Integer id) {
        String[] arr = {StatusConstant.ON_STATUS, StatusConstant.OFF_STATUS};
        if (!map.containsKey("enable") || !Arrays.asList(arr).contains(map.get("enable").toString())) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
        }
        creativeService.enableCreative(map.get("enable").toString(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 物料下创意审核
     */
    @PutMapping(value = "/package/creative/odinAudit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> auditCreative(@RequestBody OdinAuditVO odinAuditVO) {
        String[] strings = {"01", "02", "03"};
        if (!Arrays.asList(strings).contains(odinAuditVO.getAuditStatus())) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
        }
        if (odinAuditVO.getIds() == null || odinAuditVO.getIds().length == 0) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
        }

        creativeService.auditCreative(odinAuditVO.getAuditStatus(), odinAuditVO.getIds());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> getImages(@RequestParam(value = "advertiserId", required = false) Integer advertiserId,
        @RequestParam(value = "campaignId",
            required = false) Integer campaignId, @RequestParam(value = "creativeId", required = false) Integer creativeId,
        @RequestParam(value = "projectId", required = false) Integer projectId,
        @RequestParam(value = "sizeId", required = false) Integer sizeId) {
        if (advertiserId == null && campaignId == null && creativeId == null && projectId == null && sizeId == null) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
        }
        List<ImageVo> images = creativeService.getImages(advertiserId, campaignId, creativeId, projectId, sizeId);
        PaginationResponse result = new PaginationResponse(images, null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/videos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> getVideos(@RequestParam(value = "advertiserId", required = false) Integer advertiserId,
        @RequestParam(value = "campaignId", required = false) Integer campaignId,
        @RequestParam(value = "creativeId", required = false) Integer creativeId,
        @RequestParam(value = "projectId", required = false) Integer projectId,
        @RequestParam(value = "sizeId", required = false) Integer sizeId){
        if (campaignId == null && creativeId == null && projectId == null && sizeId == null) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
        }
        List<VideoVO> videoVos = creativeService.getVideos(advertiserId,campaignId, creativeId, projectId,sizeId);
        PaginationResponse result = new PaginationResponse(videoVos, null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改策略下创意价格
     *
     * @param id 策略-创意关联ID
     * @param map 价格
     */
    @PutMapping(value = "/policy/bid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePolicyCreativeBid(@PathVariable Integer id, @RequestBody Map<String, Integer> map) throws Exception {
        log.info("update Policy CreativeBid {}." + id);

        Integer bid = map.get("bid");
        // bid 校验（负数校验）
        if (bid <= 0) {
            throw new DuplicateEntityException(PhrasesConstant.CREATIVE_BID_ERROR);
        }

        creativeService.updatePolicyCreativeBid(id, bid);

        return ResponseEntity.ok(null);
    }
}
