package com.pxene.odin.cloud.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.common.constant.TargetTypeConstant;
import com.pxene.odin.cloud.common.enumeration.CampaignState;
import com.pxene.odin.cloud.common.enumeration.PolicyState;
import com.pxene.odin.cloud.common.util.CamelCaseUtil;
import com.pxene.odin.cloud.common.util.DateUtils;
import com.pxene.odin.cloud.domain.model.AdvertiserModel;
import com.pxene.odin.cloud.domain.model.CampaignKpiModel;
import com.pxene.odin.cloud.domain.model.CampaignModel;
import com.pxene.odin.cloud.domain.model.CampaignTargetingModel;
import com.pxene.odin.cloud.domain.model.PolicyModel;
import com.pxene.odin.cloud.domain.model.ProjectModel;
import com.pxene.odin.cloud.domain.vo.CampaignKpiVO;
import com.pxene.odin.cloud.domain.vo.CampaignTargetVO;
import com.pxene.odin.cloud.domain.vo.CampaignTargetingVO;
import com.pxene.odin.cloud.domain.vo.CampaignVO;
import com.pxene.odin.cloud.domain.vo.PolicyKpiVO;
import com.pxene.odin.cloud.domain.vo.PolicyTargetVO;
import com.pxene.odin.cloud.domain.vo.PolicyVO;
import com.pxene.odin.cloud.domain.vo.RegionVO;
import com.pxene.odin.cloud.domain.vo.RegionVO.City;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.repository.mapper.basic.AdvertiserMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignKpiMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignTargetingMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PackageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ProjectMapper;
import com.pxene.odin.cloud.web.api.BasicRegionClient;
import com.pxene.odin.cloud.web.controller.CampaignController;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CampaignService extends BaseService {

	@Autowired
	CampaignMapper campaignMapper;
	@Autowired
	CampaignKpiMapper campaignKpiMapper;
	@Autowired
	CampaignTargetingMapper campaignTargetingMapper;
	@Autowired
	AdvertiserMapper advertiserMapper;
	@Autowired
	ProjectMapper projectMapper;

	@Autowired
	PolicyService policyService;
	@Autowired
	RedisService redisService;
	@Autowired
	PolicyMapper policyMapper;
	@Autowired
	PackageMapper packageMapper;
    @Autowired
    BasicRegionClient basicRegionClient;

	public CampaignModel findByNameAndAdvertiserId(String name,Integer advertiserId) {

		List<CampaignModel> list = campaignMapper.selectByNameAndAdvertiserId(name,advertiserId);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public boolean isNameExist(CampaignVO campaignVO) {
		Integer projectId = campaignVO.getProjectId();
		ProjectModel projectModel = projectMapper.selectProjectById(projectId);
		return findByNameAndAdvertiserId(campaignVO.getName(),projectModel.getAdvertiserId()) != null;
	}

	public CampaignModel saveCampaign(CampaignVO campaignVO) throws Exception {
		CampaignModel campaignModel = modelMapper.map(campaignVO, CampaignModel.class);
		//开关默认为关闭
		campaignModel.setEnable(StatusConstant.OFF_STATUS);
		//运营端审核状态默认为已审核
		campaignModel.setAuditStatus(StatusConstant.AUDIT_CAMPAIGN_AUDIT_APPROVED);
		//状态字段
		campaignModel.setStatus("0");

		campaignMapper.insert(campaignModel);
		Integer campaignId = campaignModel.getId();

		CampaignKpiVO[] kpiVoArr = campaignVO.getKpi();
		if(kpiVoArr != null){
			for (CampaignKpiVO campaignKpiVO : kpiVoArr) {
				CampaignKpiModel campaignKpiModel = modelMapper.map(campaignKpiVO, CampaignKpiModel.class);
				campaignKpiModel.setCampaignId(campaignId);
				campaignKpiMapper.insert(campaignKpiModel);
			}
		}

		CampaignTargetVO targeting = campaignVO.getTargeting();
		if (targeting != null) {

			// 地域定向
			String[] regionIdArr = targeting.getRegion();
			if (regionIdArr != null) {
				for (String regionId : regionIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.REGION_TAGET);
					targetingModel.setValue(regionId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 人群包定向
			String[] populationIdArr = targeting.getPopulation();
			if (populationIdArr != null) {
				for (String populationId : populationIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.POPULATION_TAGET);
					targetingModel.setValue(populationId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 网络定向
			String[] networkArr = targeting.getNetwork();
			if (networkArr != null) {
				for (String networkId : networkArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.NETWORK_TAGET);
					targetingModel.setValue(networkId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 运营商定向
			String[] carrierArr = targeting.getCarrier();
			if (carrierArr != null) {
				for (String carrierId : carrierArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.CARRIER_TAGET);
					targetingModel.setValue(carrierId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 设备平台定向:
			String[] deviceArr = targeting.getDevice();
			if (deviceArr != null) {
				for (String deviceId : deviceArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.DEVICE_TAGET);
					targetingModel.setValue(deviceId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 操作系统定向
			String[] osArr = targeting.getOs();
			if (osArr != null) {
				for (String osId : osArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.OS_TAGET);
					targetingModel.setValue(osId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 手机品牌定向
			String[] brandArr = targeting.getBrand();
			if (brandArr != null) {
				for (String brandId : brandArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.BRAND_TAGET);
					targetingModel.setValue(brandId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 渠道定向
			String[] adxArr = targeting.getAdx();
			if (adxArr != null) {
				for (String adxId : adxArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.ADX_TAGET);
					targetingModel.setValue(adxId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 定价合同定向
			String[] contractArr = targeting.getContract();
			if (contractArr != null) {
				for (String contractId : contractArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.CONTRACT_TAGET);
					targetingModel.setValue(contractId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 媒体类型定向
			CampaignTargetingVO appType = targeting.getAppType();
			if (appType != null && appType.getValue() != null) {
				String[] appTypeIdArr = appType.getValue();
				for (String appTypeId : appTypeIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.APP_TYPE_TAGET);
					targetingModel.setIsInclude(appType.getIsInclude());
					targetingModel.setValue(appTypeId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 媒体定向
			CampaignTargetingVO app = targeting.getApp();
			if (app != null && app.getValue() != null) {
				String[] appIdArr = app.getValue();
				for (String appId : appIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(campaignId);
					targetingModel.setType(TargetTypeConstant.APP_TAGET);
					targetingModel.setIsInclude(app.getIsInclude());
					targetingModel.setValue(appId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}
		}

		//活动的开始时间在当前时间之前
		if(StatusConstant.START_DATE_BEFORE.equals(campaignStartDate(campaignId))){
			log.debug("<=DSP-Advertiser=> write Campaign Control {} Start.", campaignId);
			//活动的投放控制策略写入redis
			redisService.writeCampaignControl(campaignId);
			//将活动id写入到dsp_campaignids中
			redisService.writeCampaignIds(campaignId);
			log.debug("<=DSP-Advertiser=> write Campaign Control {} Complete.", campaignId);
		}

		return campaignModel;
	}
	
	public List<CampaignVO> listAllCampaigns(Integer projectId, Long adStartDate, Long adEndDate, String advertiserName,
			Integer id, String name, String status, String auditStatus, Long startDate, Long endDate, String sortKey,
			String sortType) {
		Map<String, Object> map = new HashMap<>();
		map.put("projectId", projectId);
		String adStartDateFormat = adStartDate==null ? null : new SimpleDateFormat("yyyy-MM-dd").format(new Date(adStartDate));
		String adEndDateFormat = adEndDate==null ? null : new SimpleDateFormat("yyyy-MM-dd").format(new Date(adEndDate));
		map.put("adStartDate", adStartDateFormat);
		map.put("adEndDate", adEndDateFormat);
		map.put("advertiserName", advertiserName);
		map.put("id", id);
		map.put("name", name);
		//map.put("status", status);
		map.put("auditStatus", auditStatus);

		if (sortKey == null || sortKey.isEmpty()) {
			map.put("sortKey", null);
		} else {
			sortKey = CamelCaseUtil.camelToUnderline(sortKey);
			map.put("sortKey", sortKey);
			if (sortType != null && sortType.equals(StatusConstant.SORT_TYPE_ASC)) {
				map.put("sortType", "ASC");
			} else if (sortType != null && sortType.equals(StatusConstant.SORT_TYPE_DESC)) {
				map.put("sortType", "DESC");
			} else {
				throw new IllegalArgumentException(PhrasesConstant.LACK_NECESSARY_PARAM);
			}
		}

		List<CampaignModel> findAllCampaigns = campaignMapper.findAllCampaigns(map);

		List<CampaignVO> campaignVOList = new ArrayList<>();
		for (CampaignModel campaignModel : findAllCampaigns) {
			CampaignVO campaignVO = modelMapper.map(campaignModel, CampaignVO.class);
			//查询活动状态
			CampaignState state = getState(campaignModel.getId());
			campaignVO.setStatus(state.getCode());
			
			// 查询广告项目
			ProjectModel projectModel = projectMapper.selectProjectById(campaignModel.getProjectId());
			campaignVO.setProjectName(projectModel.getName());
			// 查询广告主
			AdvertiserModel advertiserModel = advertiserMapper.selectByPrimaryKey(projectModel.getAdvertiserId());
			campaignVO.setAdvertiserId(advertiserModel.getId());
			campaignVO.setAdvertiserName(advertiserModel.getName());
			
			//查询投放中策略数量
			map.put("campaignId", campaignModel.getId());
			Integer advertisingAmount=0;
			List<PolicyModel> policys = policyMapper.findAllPolicys(map);
			for (PolicyModel policyModel : policys) {
				PolicyState policyState = policyService.getState(policyModel.getId());
				if(policyState !=null && StatusConstant.POLICY_LAUNCHING.equals(policyState.getCode())){
					advertisingAmount++;
				}
			}
			campaignVO.setAdvertisingAmount(advertisingAmount);
			
			// 查询总策略数量
			Integer policyCount = policyMapper.selectPolicyCountByCampaignId(campaignModel.getId());
			campaignVO.setTotalAmount(policyCount);
			// 查询物料包数量
			Integer packageCount = packageMapper.selectPackageCountByCampaignId(campaignModel.getId());
			campaignVO.setPackageAmount(packageCount);
			
			// 数据信息查询
			// TODO
			
			//添加根据状态查询特殊情况(已暂停,投放中,已结束)
			if(status !=null){
				//已暂停包括广告主已暂停、已暂停、已暂停-未到投放周期、已暂停-不在投放时段、已暂停-已到日KPI、已暂停-已到日成本
				if(StatusConstant.CAMPAIGN_SUSPENDED.equals(status)){
					if(StatusConstant.CAMPAIGN_SUSPENDED.equals(state.getCode())
							||StatusConstant.CAMPAIGN_OUT_OF_CYCLE.equals(state.getCode())
							||StatusConstant.CAMPAIGN_OUT_OF_PHASE.equals(state.getCode())
							||StatusConstant.CAMPAIGN_OUT_OF_KPI.equals(state.getCode())
							||StatusConstant.CAMPAIGN_OUT_OF_COST.equals(state.getCode())){
						campaignVOList.add(campaignVO);
					}
				} else if(status.equals(state.getCode())){
					campaignVOList.add(campaignVO);
				}
			}else{
				campaignVOList.add(campaignVO);
			}
		}
 		return campaignVOList;
	}

	public CampaignModel selectCampaignById(Integer campaignId){
		return campaignMapper.selectByPrimaryKey(campaignId);
	}

	public CampaignVO findById(Integer id) {

		CampaignModel campaignModel = selectCampaignById(id);
		if (campaignModel == null) {
			return null;
		}

		CampaignVO campaignVO = modelMapper.map(campaignModel, CampaignVO.class);

		// kpi
		List<CampaignKpiModel> kpiModelList = campaignKpiMapper.selectByCampaignId(campaignModel.getId());
		if (kpiModelList != null) {
			int len = kpiModelList.size();
			CampaignKpiVO[] kpiVOArr = new CampaignKpiVO[len];
			for (int i = 0; i < len; i++) {
				CampaignKpiVO campaignKpiVO = modelMapper.map(kpiModelList.get(i), CampaignKpiVO.class);
				kpiVOArr[i] = campaignKpiVO;
			}
			campaignVO.setKpi(kpiVOArr);
		}

		CampaignTargetVO targetVO = new CampaignTargetVO();
		campaignVO.setTargeting(targetVO);

		// adx定向
		List<CampaignTargetingModel> adxList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(),TargetTypeConstant.ADX_TAGET);
		if (adxList != null) {
			int adxLen = adxList.size();
			String[] adx = new String[adxLen];
			for (int i = 0; i < adxLen; i++) {
				adx[i] = adxList.get(i).getValue();
			}
			targetVO.setAdx(adx);
		}
		// 定向合同定向
		List<CampaignTargetingModel> contractList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.CONTRACT_TAGET);
		if (contractList != null) {
			int contractLen = contractList.size();
			String[] contract = new String[contractLen];
			for (int i = 0; i < contractLen; i++) {
				contract[i] = contractList.get(i).getValue();
			}
			targetVO.setContract(contract);
		}
		// 品牌定向
		List<CampaignTargetingModel> brandList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.BRAND_TAGET);
		if (brandList != null) {
			int brandLen = brandList.size();
			String[] brand = new String[brandLen];
			for (int i = 0; i < brandLen; i++) {
				brand[i] = brandList.get(i).getValue();
			}
			targetVO.setBrand(brand);
		}
		// 运营商定向
		List<CampaignTargetingModel> carrierList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.CARRIER_TAGET);
		if (carrierList != null) {
			int carrierLen = carrierList.size();
			String[] carrier = new String[carrierLen];
			for (int i = 0; i < carrierLen; i++) {
				carrier[i] = carrierList.get(i).getValue();
			}
			targetVO.setCarrier(carrier);
		}
		// 设备定向
		List<CampaignTargetingModel> deviceList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.DEVICE_TAGET);
		if (deviceList != null) {
			int deviceLen = deviceList.size();
			String[] device = new String[deviceLen];
			for (int i = 0; i < deviceLen; i++) {
				device[i] = deviceList.get(i).getValue();
			}
			targetVO.setDevice(device);
		}
		// 网络定向
		List<CampaignTargetingModel> networkList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.NETWORK_TAGET);
		if (networkList != null) {
			int networkLen = networkList.size();
			String[] network = new String[networkLen];
			for (int i = 0; i < networkLen; i++) {
				network[i] = networkList.get(i).getValue();
			}
			targetVO.setNetwork(network);
		}
		// 系统定向
		List<CampaignTargetingModel> osList = campaignTargetingMapper.selectBycampaignIdAndType(campaignModel.getId(),
				TargetTypeConstant.OS_TAGET);
		if (osList != null) {
			int osLen = osList.size();
			String[] os = new String[osLen];
			for (int i = 0; i < osLen; i++) {
				os[i] = osList.get(i).getValue();
			}
			targetVO.setOs(os);
		}
		// 人群包定向
		List<CampaignTargetingModel> populationList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.POPULATION_TAGET);
		if (populationList != null) {
			int populationLen = populationList.size();
			String[] population = new String[populationLen];
			for (int i = 0; i < populationLen; i++) {
				population[i] = populationList.get(i).getValue();
			}
			targetVO.setPopulation(population);
		}
		// 地域定向
		List<CampaignTargetingModel> regionList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.REGION_TAGET);
		if (regionList != null) {
			int regionLen = regionList.size();
			String[] region = new String[regionLen];
			for (int i = 0; i < regionLen; i++) {
				region[i] = regionList.get(i).getValue();
			}
			targetVO.setRegion(region);
		}
		// 媒体分类
		List<CampaignTargetingModel> appTypeList = campaignTargetingMapper
				.selectBycampaignIdAndType(campaignModel.getId(), TargetTypeConstant.APP_TYPE_TAGET);
		if (appTypeList != null && !appTypeList.isEmpty()) {
			int appTypeLen = appTypeList.size();
			String[] appType = new String[appTypeLen];
			for (int i = 0; i < appTypeLen; i++) {
				appType[i] = appTypeList.get(i).getValue();
			}
			CampaignTargetingVO campaignTargetingVO = new CampaignTargetingVO();
			campaignTargetingVO.setIsInclude(appTypeList.get(0).getIsInclude());
			campaignTargetingVO.setValue(appType);
			targetVO.setAppType(campaignTargetingVO);
		}else{
			CampaignTargetingVO campaignTargetingVO = new CampaignTargetingVO();
			targetVO.setAppType(campaignTargetingVO);
		}
		// 媒体名称
		List<CampaignTargetingModel> appList = campaignTargetingMapper.selectBycampaignIdAndType(campaignModel.getId(),
				TargetTypeConstant.APP_TAGET);
		if (appList != null && !appList.isEmpty()) {
			int appLen = appList.size();
			String[] app = new String[appLen];
			for (int i = 0; i < appLen; i++) {
				app[i] = appList.get(i).getValue();
			}
			CampaignTargetingVO campaignTargetingVO = new CampaignTargetingVO();
			campaignTargetingVO.setIsInclude(appList.get(0).getIsInclude());
			campaignTargetingVO.setValue(app);
			targetVO.setApp(campaignTargetingVO);
		}else{
			CampaignTargetingVO campaignTargetingVO = new CampaignTargetingVO();
			targetVO.setApp(campaignTargetingVO);
		}

		// 查询广告项目
		ProjectModel projectModel = projectMapper.selectProjectById(campaignModel.getProjectId());
		campaignVO.setProjectName(projectModel.getName());
		// 查询广告主
		AdvertiserModel advertiserModel = advertiserMapper.selectByPrimaryKey(projectModel.getAdvertiserId());
		campaignVO.setAdvertiserId(advertiserModel.getId());
		campaignVO.setAdvertiserName(advertiserModel.getName());

		// 今日推广活动数据
		// TODO

		// 累计推广活动数据
		// TODO

		return campaignVO;
	}

	public void updateCampaign(Integer id,CampaignVO currentCampaignVO, CampaignVO campaignVO) throws Exception {

		CampaignModel campaignModel = modelMapper.map(campaignVO, CampaignModel.class);
		campaignMapper.updateByPrimaryKey(campaignModel);

		CampaignKpiVO[] kpiVoArr = campaignVO.getKpi();
		if (kpiVoArr != null) {
			campaignKpiMapper.deleteByCampaignId(id);
			for (CampaignKpiVO campaignKpiVO : kpiVoArr) {
				CampaignKpiModel campaignKpiModel = modelMapper.map(campaignKpiVO, CampaignKpiModel.class);
				campaignKpiModel.setCampaignId(id);
				campaignKpiMapper.insert(campaignKpiModel);
			}
		}

		CampaignTargetVO targeting = campaignVO.getTargeting();
		if (targeting != null) {

			// 地域定向
			String[] regionIdArr = targeting.getRegion();
			if (regionIdArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.REGION_TAGET);
				for (String regionId : regionIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.REGION_TAGET);
					targetingModel.setValue(regionId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 人群包定向
			String[] populationIdArr = targeting.getPopulation();
			if (populationIdArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.POPULATION_TAGET);
				for (String populationId : populationIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.POPULATION_TAGET);
					targetingModel.setValue(populationId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 网络定向
			String[] networkArr = targeting.getNetwork();
			if (networkArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.NETWORK_TAGET);
				for (String networkId : networkArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.NETWORK_TAGET);
					targetingModel.setValue(networkId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 运营商定向
			String[] carrierArr = targeting.getCarrier();
			if (carrierArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.CARRIER_TAGET);
				for (String carrierId : carrierArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.CARRIER_TAGET);
					targetingModel.setValue(carrierId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 设备平台定向:
			String[] deviceArr = targeting.getDevice();
			if (deviceArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.DEVICE_TAGET);
				for (String deviceId : deviceArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.DEVICE_TAGET);
					targetingModel.setValue(deviceId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 操作系统定向
			String[] osArr = targeting.getOs();
			if (osArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.OS_TAGET);
				for (String osId : osArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.OS_TAGET);
					targetingModel.setValue(osId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 手机品牌定向
			String[] brandArr = targeting.getBrand();
			if (brandArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.BRAND_TAGET);
				for (String brandId : brandArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.BRAND_TAGET);
					targetingModel.setValue(brandId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 渠道定向
			String[] adxArr = targeting.getAdx();
			if (adxArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.ADX_TAGET);
				for (String adxId : adxArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.ADX_TAGET);
					targetingModel.setValue(adxId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 定价合同定向
			String[] contractArr = targeting.getContract();
			if (contractArr != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.CONTRACT_TAGET);
				for (String contractId : contractArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.CONTRACT_TAGET);
					targetingModel.setValue(contractId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 媒体类型定向
			CampaignTargetingVO appType = targeting.getAppType();
			if (appType != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.APP_TYPE_TAGET);
				String[] appTypeIdArr = appType.getValue() == null ? new String[0]  : appType.getValue();
				for (String appTypeId : appTypeIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.APP_TYPE_TAGET);
					targetingModel.setIsInclude(appType.getIsInclude());
					targetingModel.setValue(appTypeId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}

			// 媒体定向
			CampaignTargetingVO app = targeting.getApp();
			if (app != null) {
				campaignTargetingMapper.deleteByCampaignIdAndType(id, TargetTypeConstant.APP_TAGET);
				String[] appIdArr = app.getValue()==null ? new String[0] : app.getValue();
				for (String appId : appIdArr) {
					CampaignTargetingModel targetingModel = new CampaignTargetingModel();
					targetingModel.setCampaignId(id);
					targetingModel.setType(TargetTypeConstant.APP_TAGET);
					targetingModel.setIsInclude(app.getIsInclude());
					targetingModel.setValue(appId);
					campaignTargetingMapper.insert(targetingModel);
				}
			}
		}
		
			//未到活动投放时间
			if(StatusConstant.START_DATE_AFTER
					.equals(policyService.checkStartDate(new Date(currentCampaignVO.getStartDate())))){
				//若编辑活动策略信息,不操作redis
				//若编辑活动投放周期
				//if(isUpdateCampaignDate(currentCampaignVO, campaignVO)){
					//活动投放周期的开始时间在当前时间之前
					if(StatusConstant.START_DATE_BEFORE.equals(campaignStartDate(id))){
						log.debug("<=DSP-Advertiser=> write Campaign Control {} Start.", id);
						//将入dsp_campaign_control_活动的投放控制策略写入redis
						redisService.writeCampaignControl(id);
						//将活动id写入到dsp_campaignids中
						redisService.writeCampaignIds(id);
						log.debug("<=DSP-Advertiser=> write Campaign Control {} Complete.", id);
					}
				//}
			}
			//在活动投放周期
			if(policyService
					.isPeriod(new Date(currentCampaignVO.getStartDate()), new Date(currentCampaignVO.getEndDate()))){
				//编辑活动策略信息
				//if(isUpdateCampaignControl(currentCampaignVO, campaignVO)){
					log.debug("<=DSP-Advertiser=> write Campaign Control {} Start.", id);
					redisService.writeCampaignControl(id);
					log.debug("<=DSP-Advertiser=> write Campaign Control {} Complete.", id);
				//}
				//编辑活动投放周期,不修改redis
			}
			//活动投放已结束
			if(policyService
					.isEndDate(new Date(currentCampaignVO.getEndDate()))){
				//编辑活动策略信息,不操作redis
				//编辑活动投放周期
				//if(isUpdateCampaignDate(currentCampaignVO, campaignVO)){
					//活动投放周期的开始时间为今天
					if(!isCampaignEndDate(id)){
						log.debug("<=DSP-Advertiser=> write Campaign Control {} Start.", id);
						//活动的投放控制策略写入redis : dsp_campaign_control_(campaignid)
						redisService.writeCampaignControl(id);
						//将活动id写入到dsp_campaignids中
						redisService.writeCampaignIds(id);
						log.debug("<=DSP-Advertiser=> write Campaign Control {} Complete.", id);
					}
				//}
			}
	}

	public boolean isUpdateNameExist(CampaignVO campaignVO) {

		Integer projectId = campaignVO.getProjectId();
		ProjectModel projectModel = projectMapper.selectProjectById(projectId);

		Map<String, Object> map = new HashMap<>();
		map.put("id", campaignVO.getId());
		map.put("name", campaignVO.getName());
		map.put("advertiserId", projectModel.getAdvertiserId());
		List<CampaignModel> campaignModels = campaignMapper.selectByNotId(map);

		return campaignModels.size() != 0;
	}
	
	public void checkSaveCampaignRules(CampaignVO campaignVO) {
		 //开始时间在当前时间之后 
        if(DateUtils.getCurrenDay().getTime()>campaignVO.getStartDate().longValue()){
        	throw new DuplicateEntityException(PhrasesConstant.START_DATE_NOT_BEFORE);
        }
	}

	public void checkCampaignRules(CampaignVO campaignVO) throws Exception{
		CampaignKpiVO[] kpi = campaignVO.getKpi();
		Long totalImpression = campaignVO.getTotalImpression();
		Long totalClick = campaignVO.getTotalClick();
		Long totalBudget = campaignVO.getTotalBudget();
		Long impressionSum = 0L;
		Long clickSum = 0L;
		Long budgeSum = 0L;
		
		if(totalImpression == null && totalClick == null){
			throw new DuplicateEntityException(PhrasesConstant.TOTAL_IMPRESSION_CLICK_NOT_NULL);
		}

		if (kpi != null) {
			for (CampaignKpiVO campaignKpiVO : kpi) {
				long period = campaignKpiVO.getPeriod();
				Long dailyImpression = campaignKpiVO.getDailyImpression();
				Long dailyClick = campaignKpiVO.getDailyClick();
				Long dailyBudget = campaignKpiVO.getDailyBudget();
				if ((period == 0 && dailyImpression != null && dailyImpression>0)
						|| (period == 0 && dailyClick != null && dailyClick>0)
						|| (period == 0 && dailyBudget != null && dailyBudget>0)) {

					SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
					String format = sdf.format(campaignKpiVO.getDay());
					throw new DuplicateEntityException(format+PhrasesConstant.PERIOD_EMPTY_KPI_EXIST);
				}
				if ((period > 0 && dailyImpression == null )
						&& (period > 0 && dailyClick == null )) {

					SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
					String format = sdf.format(campaignKpiVO.getDay());
					throw new DuplicateEntityException(format+PhrasesConstant.PERIOD_EXIST_KPI_EMPTY);
				}
				if (dailyImpression != null) {
					impressionSum += dailyImpression;
				}
				if (dailyClick != null) {
					clickSum += dailyClick;
				}
				if (dailyBudget != null) {
					budgeSum += dailyBudget;
				}
			}
		}

		if (totalImpression != null && !totalImpression .equals(impressionSum) ) {
			throw new DuplicateEntityException(PhrasesConstant.TOTAL_KPI_NOT_IDENTICAL);
		}
		if (totalClick != null && !totalClick .equals(clickSum) ) {
			throw new DuplicateEntityException(PhrasesConstant.TOTAL_KPI_NOT_IDENTICAL);
		}
		if (totalBudget != null && !totalBudget .equals(budgeSum) ) {
			throw new DuplicateEntityException(PhrasesConstant.TOTAL_BUDGET_NOT_IDENTICAL);
		}
		
		//场景定向
		String scenePath = campaignVO.getScenePath();
		String sceneRadius = campaignVO.getSceneRadius();
		if(scenePath == null && sceneRadius != null){
			throw new DuplicateEntityException(PhrasesConstant.SCENE_NO_PATH);
		}
		if(scenePath != null && sceneRadius == null){
			throw new DuplicateEntityException(PhrasesConstant.SCENE_NO_RADIUS);
		}
	}

	public void auditCampaign(Integer id, Map<String,String> map) {
		CampaignModel campaignModel = new CampaignModel();
		campaignModel.setId(id);
		campaignModel.setAuditStatus(map.get("auditStatus"));
		campaignMapper.updateByIdSelective(campaignModel);
	}

	public void checkUpdateCampaignRules(CampaignVO currentCampaignVO, CampaignVO campaignVO) throws Exception{

		long currentStartDate = currentCampaignVO.getStartDate();
		long currentEndDate = currentCampaignVO.getEndDate();
		long startDate = campaignVO.getStartDate();
		long endDate = campaignVO.getEndDate();
		long time = DateUtils.getCurrenDay().getTime();
		
		//未开始投放前
		if(time < currentStartDate){
			if(currentStartDate!=startDate && startDate<time){
				throw new DuplicateEntityException(PhrasesConstant.START_DATE_NOT_BEFORE);
			}
		}
		//投放中
		if(currentStartDate <= time && time <= currentEndDate){
			if(startDate !=currentStartDate){
				throw new DuplicateEntityException(PhrasesConstant.POLICY_START_DATE_NOT_UPDATE);
			}
			if((currentEndDate!=endDate && endDate<time)){
				throw new DuplicateEntityException(PhrasesConstant.POLICY_END_DATE_NOT_BEFORE_TODAY);
			}
		}
		//投放结束后
		if(currentEndDate<time){
			if(startDate !=currentStartDate){
				throw new DuplicateEntityException(PhrasesConstant.POLICY_START_DATE_NOT_UPDATE);
			}
			if((currentEndDate!=endDate && endDate<time)){
				throw new DuplicateEntityException(PhrasesConstant.POLICY_END_DATE_NOT_BEFORE_TODAY);
			}
		}
		
		//查询活动下的策略
		Integer policyCount = policyMapper.selectPolicyCountByCampaignId(campaignVO.getId());
		if(policyCount>0){
			//投放开始日期不可编辑，投放结束日期只能延长不能缩短
			if(startDate != currentStartDate || endDate < currentEndDate){
				throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
			}
			//总KPI和日KPI都只能往大了编辑，即编辑之后的值不能小于当前的值
			Long currentTotalImpression = currentCampaignVO.getTotalImpression();
			Long currentTotalClick = currentCampaignVO.getTotalClick();
			Long currentTotalBudget = currentCampaignVO.getTotalBudget();
			
			Long totalImpression = campaignVO.getTotalImpression();
			Long totalClick = campaignVO.getTotalClick();
			Long totalBudget = campaignVO.getTotalBudget();
			
			if(currentTotalImpression!=null && totalImpression==null){
				throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
			}
			if(currentTotalImpression!=null && totalImpression!=null){
				if(currentTotalImpression>totalImpression){
					throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
				}
			}
			
			if(currentTotalClick!=null && totalClick==null){
				throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
			}
			if(currentTotalClick!=null && totalClick!=null){
				if(currentTotalClick>totalClick){
					throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
				}
			}
			
			if(currentTotalBudget!=null && totalBudget==null){
				throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
			}
			if(currentTotalBudget!=null && totalBudget!=null){
				if(currentTotalBudget>totalBudget){
					throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
				}
			}
			
			//比较日kpi
			CampaignKpiVO[] currentKpis = currentCampaignVO.getKpi();
			CampaignKpiVO[] kpis = campaignVO.getKpi();
		    for (int i = 0; i < currentKpis.length; i++) {
	            Long crrentDailyImpression = currentKpis[i].getDailyImpression();
	            Long currentDailyClick = currentKpis[i].getDailyClick();
	            Long currentDailyBudget = currentKpis[i].getDailyBudget();
	            Date day = currentKpis[i].getDay();
	            
	            for (int j = 0; j< kpis.length ; j++) {
	            	if(kpis[j].getDay().equals(day)){
	            		
	            		 Long dailyImpression = kpis[j].getDailyImpression();
	                     Long dailyClick = kpis[j].getDailyClick();
	                     Long dailyBudget = kpis[j].getDailyBudget();
	                     //比较日展现
	                     if(crrentDailyImpression!=null && dailyImpression==null){
	                    	 throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
	                     }
	                     if(crrentDailyImpression!=null && dailyImpression!=null){
	                    	 if(crrentDailyImpression>dailyImpression){
	                    		 throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
	                    	 }
	                     }
	                     //比较日点击
	                     if(currentDailyClick!=null && dailyClick==null){
	                    	 throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
	                     }
	                     if(currentDailyClick!=null && dailyClick!=null){
	                    	 if(currentDailyClick>dailyClick){
	                    		 throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
	                    	 }
	                     }
	                     //比较日成本
	                     if(currentDailyBudget!=null && dailyBudget==null){
	                    	 throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
	                     }
	                     if(currentDailyBudget!=null && dailyBudget!=null){
	                    	 if(currentDailyBudget>dailyBudget){
	                    		 throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
	                    	 }
	                     }
	                     //对时段校验
	                     Long currentPeriod = currentKpis[i].getPeriod();
	                     Long period = kpis[j].getPeriod();
	                     Long result= currentPeriod & period;
	                     if (!result.equals(currentPeriod)) {
	                     		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
	                     }
	            	}
				}
	         }
		    //单价不可编辑
//            if(!currentCampaignVO.getBid().equals(campaignVO.getBid())
//            		||!currentCampaignVO.getBidType().equals(campaignVO.getBidType())){
//            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
//            }
			//
        	CampaignTargetVO targeting = campaignVO.getTargeting();
        	String[] region = targeting.getRegion();
            
            CampaignTargetVO currentTargeting = currentCampaignVO.getTargeting();
            String[] currentRegion = currentTargeting.getRegion();
            	//若原设置为“不限”，则不能再编辑
            	if(currentRegion.length==0 && region.length !=0){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            	//若原设置了部分地域，则只能增加投放地域，不能减少地域
            	if(currentRegion.length!=0 && region.length !=0){
                    List<String> currentRegionList = Arrays.asList(currentRegion);
                    List<String> regionList = new ArrayList<>(Arrays.asList(region));
                    
                    //前端地域选择全省只传省ID,获取省和市信息
                    String listRegions = basicRegionClient.listRegions();
                    JsonArray jsonArray = new JsonParser().parse(listRegions).getAsJsonObject().getAsJsonArray("items");
                    List<RegionVO> regions=new Gson().fromJson(jsonArray, new TypeToken<List<RegionVO>>() {
            		}.getType());
                    
                    for (String regionId : region) {
            			if("0000".equals(StringUtils.substring(regionId, 2))){
            				for(RegionVO regionVO : regions){
            					List<String> cityIdList=new ArrayList<>();
            					if(regionId.equals(regionVO.getId())){
            						City[] citys = regionVO.getCitys();
            						if(citys != null){
            							for (City city : citys) {
            								cityIdList.add(city.getId());
            							}
            						}
            						regionList.addAll(cityIdList);
            					}
            				}
            			}
            		}
                    if (!regionList.containsAll(currentRegionList)) {
                        throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
                    }
            	}
            //人群包定向：如原设置为“不限”或“黑名单”，则不能再编辑;
            //若原设置为“只投白名单”或“优先投放白名单”，则只能增加白名单，不能减少白名单，且“优先投放白名单”时溢价率只能大于等于原值
            //TODO
            	
            //网络定向：若原设置为“不限”，则不能再编辑；若原设置不是“不限”，则只能增加设置不能减少设置
            String[] currentNetwork = currentTargeting.getNetwork();
            String[] network = targeting.getNetwork();
            List<String> currentNetworkList = Arrays.asList(currentNetwork);
            List<String> networkList = Arrays.asList(network);
            if(currentNetwork.length==0 && network.length>0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentNetwork.length!=0 && network.length!=0){
            	if(!networkList.containsAll(currentNetworkList)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }
            //运营商定向
            String[] currentCarrier = currentTargeting.getCarrier();
            String[] carrier = targeting.getCarrier();
            List<String> currentCarrierList = Arrays.asList(currentCarrier);
            List<String> carrierList = Arrays.asList(carrier);
            if(currentCarrier.length==0 && carrier.length>0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentCarrier.length!=0 && carrier.length!=0){
            	if(!carrierList.containsAll(currentCarrierList)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }
            //设备定向
            String[] currentDevice = currentTargeting.getDevice();
            String[] device = targeting.getDevice();
            List<String> currentDeviceList = Arrays.asList(currentDevice);
            List<String> deviceList = Arrays.asList(device);
            if(currentDevice.length==0 && device.length>0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentDevice.length!=0 && device.length!=0){
            	if(!deviceList.containsAll(currentDeviceList)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }
            //系统定向
            String[] currentOs = currentTargeting.getOs();
            String[] os = targeting.getOs();
            List<String> currentOsList = Arrays.asList(currentOs);
            List<String> osList = Arrays.asList(os);
            if(currentOs.length==0 && os.length>0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentOs.length!=0 && os.length!=0){
            	if(!osList.containsAll(currentOsList)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }
            //品牌定向
            String[] currentBrand = currentTargeting.getBrand();
            String[] brand = targeting.getBrand();
            List<String> currentBrandList = Arrays.asList(currentBrand);
            List<String> brandList = Arrays.asList(brand);
            if(currentBrand.length==0 && brand.length>0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentBrand.length!=0 && brand.length!=0){
            	if(!brandList.containsAll(currentBrandList)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }
            //渠道定向
            String[] currentAdx = currentTargeting.getAdx();
            String[] adx = targeting.getAdx();
            List<String> currentAdxList = Arrays.asList(currentAdx);
            List<String> adxList = Arrays.asList(adx);
            if(currentAdx.length==0 && adx.length>0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentAdx.length!=0 && adx.length!=0){
            	if(!adxList.containsAll(currentAdxList)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }
            
            //当原设置未设置场景定向时，则不能更改此设置；当原设置了场景定向时，则可以取消场景定向，或者只能编辑投放范围，且只能往大了编辑，比如原设置的范围
            String currentScenePath = currentCampaignVO.getScenePath();
            String scenePath = campaignVO.getScenePath();
            String currentSceneRadius = currentCampaignVO.getSceneRadius();
            String sceneRadius = campaignVO.getSceneRadius();
            
            if(currentScenePath == null||currentScenePath.isEmpty()){
            	if(scenePath!=null && !scenePath.isEmpty()){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            }else{
        		if((scenePath!=null && !scenePath.isEmpty()) && !currentScenePath.equals(scenePath)){
        			throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        		}
        		if(sceneRadius != null && currentSceneRadius != null){
        			if(Integer.parseInt(sceneRadius)>Integer.parseInt(currentSceneRadius)){
        				throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        			}
        		}
            }
            //媒体分类定向:选择媒体分类时，不能去掉原设置的媒体分类，只能增加媒体分类；排除媒体分类时，不能增加媒体分类，只能去掉原设置的媒体分类
            CampaignTargetingVO currentAppType = currentTargeting.getAppType();
            CampaignTargetingVO appType = targeting.getAppType();
            String currentIsInclude = currentAppType == null ? null : currentAppType.getIsInclude();
            String isInclude = appType == null ? null : appType.getIsInclude();
            
            if(currentIsInclude!=null){
	            if(isInclude!=null && !StringUtils.equals(currentIsInclude, isInclude)){
	            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
	            }
            }else{
            	if(isInclude!=null){
	            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
	            }
            }
            
            if(StatusConstant.INCLUDE_TYPE_VALUE.equals(isInclude)) {
            	 List<String> currentAppTypeList = Arrays.asList(currentAppType.getValue());
                 List<String> appTypeList = Arrays.asList(appType.getValue());
                 if(!appTypeList.containsAll(currentAppTypeList)){
                	 throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
                 }
            }
            if(StatusConstant.EXCLUDE_TYPE_VALUE.equals(isInclude)) {
           	 	List<String> currentAppTypeList = Arrays.asList(currentAppType.getValue());
                List<String> appTypeList = Arrays.asList(appType.getValue());
                if(!currentAppTypeList.containsAll(appTypeList)){
               	 throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
                }
           }
            
           //选择媒体时，不能去掉原设置的媒体，只能增加媒体；排除媒体时，不能增加媒体，只能去掉原设置的媒体；
            CampaignTargetingVO currentApp = currentTargeting.getApp();
            CampaignTargetingVO app = targeting.getApp();
            
            String[] currentValues = currentApp.getValue();
            String[] values = app.getValue()==null ? new String[0] : app.getValue();
            if(currentValues == null && values.length!=0){
            	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
            if(currentValues !=null && values.length!=0){
            	String currentAppIsInclude = currentApp.getIsInclude();
            	String appIsInclude = app.getIsInclude();
            	
            	if(!StringUtils.equals(currentAppIsInclude, appIsInclude)){
            		throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            	}
            	
	        	if(StatusConstant.INCLUDE_TYPE_VALUE.equals(appIsInclude)) {
	           	 List<String> currentAppList = Arrays.asList(currentValues);
	                List<String> appList = Arrays.asList(values);
	                if(!appList.containsAll(currentAppList)){
	               	 throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
	                }
	            }
	            if(StatusConstant.EXCLUDE_TYPE_VALUE.equals(appIsInclude)) {
	          	 	List<String> currentAppList = Arrays.asList(currentValues);
	               List<String> appList = Arrays.asList(values);
	               if(!currentAppList.containsAll(appList)){
	              	 throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
	               }
	            } 
            }
            
		}
	}

	/**
	 * 判断活动周期
	 * @param campaignId
	 * @return
	 */
	public boolean isCampaignPeriod(Integer campaignId) {
		// 查询活动信息
		CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
		// 活动的周期
		Date startDate = campaign.getStartDate();
		Date endDate = campaign.getEndDate();
		// 判断投放周期，在投放周期返回true
		return policyService.isPeriod(startDate, endDate);
	}

	/**
	 * 判断活动的投放开始时间是在当前时间之前/之后
	 * @param startDate 活动投放的开始时间
	 * @return
	 */
	public String campaignStartDate(Integer campaignId) {
		// 查询活动信息
		CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
		// 活动的周期开始时间
		Date startDate = campaign.getStartDate();
		// 开始投放的时间类型
		String strDateType = policyService.checkStartDate(startDate);
		return strDateType;
	}

	/**
	 * 判断活动的投放开始时间是今天
	 * @param campaignId
	 * @return
	 */
	public boolean isCampaignTodayStart(Integer campaignId) {
		// 查询活动信息
		CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
		// 活动的周期开始时间
		Date startDate = campaign.getStartDate();
		// 如果是今天，返回ture
		return policyService.isTodayStart(startDate);
	}

	/**
	 * 判断活动的开关是否打开
	 * @param campaignId 活动id
	 * @return
	 */
	public boolean isOpenCampaign(Integer campaignId) {
		// 查询活动信息
		CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
		// 活动的开关状态
		String enable = campaign.getEnable();
		// 如果活动的开关打开，返回ture
		return policyService.isOpenSwitch(enable);
	}

	/**
	 * 判断当前活动是否在投放定向时间内
	 * @param campaignId
	 * @return
	 */
	public boolean isOnTargetTime(Integer campaignId) {
		// 当前天
		Date day = DateUtils.getCurrenDay();
		// 查询今天的时间定向
		CampaignKpiModel campaignKpi = campaignKpiMapper.selectByPrimaryKey(campaignId,day);
		if (campaignKpi != null) {
			Integer period = campaignKpi.getPeriod().intValue();
			// 将十段转成二进制
			String periods = Integer.toBinaryString(period);
			// 获取二进制的长度
			int length = periods.length();
			// 24位二进制
			for (int i = 0; i < 24-length; i++) {
				periods = 0 + periods;
			}
			// 获取当前小时
			int currentHour = new Date().getHours();
			// 获取二进制数第hours个的值，判断
			if (periods.charAt(currentHour) == '1') {
				 // 如果当前小时为1表示选中
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 判断活动日成本是否已达上限
	 * @param campaignId
	 * @return
	 */
	public boolean isOverDailyBudget(Integer campaignId) {
//		// 当前天
//		Date day = DateUtils.getCurrenDay();
//		// 查询数据库中日成本
//		CampaignKpiModel campaignKpi = campaignKpiMapper.selectByPrimaryKey(campaignId, day);
//		if (campaignKpi != null) {
//			Long dailyBudget = campaignKpi.getDailyBudget();
//			// TODO : 取数据模块中的日成本
//			Long dailyCost = null;
//			if (dailyBudget.longValue() <=  dailyCost.longValue()) {
//				return true;
//			} else {
//				return false;
//			}
//		}
		return false;
	}

	/**
	 * 判断Kpi是否已到上限
	 * @param campaignId
	 * @return
	 */
	public boolean isOverDailyKpi(Integer campaignId) {
//		// 当前天
//		Date day = DateUtils.getCurrenDay();
//		// 查询数据库中日成本
//		CampaignKpiModel campaignKpi = campaignKpiMapper.selectByPrimaryKey(campaignId, day);
//		if (campaignKpi != null) {
//			Long dailyImpression = campaignKpi.getDailyImpression();
//			Long dailyClick = campaignKpi.getDailyClick();
//			// TODO : 取数据模块中的日点击、日展现
//			Long impressionData = null;
//			Long clickData = null;
//			if (dailyImpression.longValue() <= impressionData.longValue() && dailyClick.longValue() <= clickData.longValue()) {
//				return true;
//			} else {
//				return false;
//			}
//		}
		return false;
	}

	/**
	 * 判断活动投放周期是否已结束
	 * @param campaignId
	 * @return
	 */
	public boolean isCampaignEndDate(Integer campaignId) {
		// 查询活动信息
		CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
		// 活动的结束时间
		Date endDate = campaign.getEndDate();
		// 如果已结束返回true
		return policyService.isEndDate(endDate);
	}

	/**
	 * 判断是否编辑活动投放周期
	 * @param currentCampaignVO
	 * @param campaignVO
	 * @return
	 */
	public boolean isUpdateCampaignDate(CampaignVO currentCampaignVO,CampaignVO campaignVO){
		return currentCampaignVO.getStartDate().longValue()!=campaignVO.getStartDate().longValue()
				||currentCampaignVO.getEndDate().longValue()!=campaignVO.getEndDate().longValue();
	}

	/**
	 * 判断是否编辑活动策略信息
	 * @param currentCampaignVO
	 * @param campaignVO
	 * @return
	 */
	public boolean isUpdateCampaignControl(CampaignVO currentCampaignVO, CampaignVO campaignVO){
		boolean result=false;
		Long currentTotalImpression = currentCampaignVO.getTotalImpression();
		Long currentTotalClick = currentCampaignVO.getTotalClick();
		Long currentTotalBudget = currentCampaignVO.getTotalBudget();

		Long totalImpression = campaignVO.getTotalImpression();
		Long totalClick = campaignVO.getTotalClick();
		Long totalBudget = campaignVO.getTotalBudget();
		//比较总展现量
		if(!ObjectUtils.equals(currentTotalImpression,totalImpression)){
			result=true;
		}
		//比较总点击量
		if(!ObjectUtils.equals(currentTotalClick,totalClick)){
			result=true;
		}
		//比较总预算
		if(!ObjectUtils.equals(currentTotalBudget,totalBudget)){
			result=true;
		}

		CampaignKpiVO[] currentKpis = currentCampaignVO.getKpi();
		CampaignKpiVO[] kpis = campaignVO.getKpi();
		for(int i=0; i<currentKpis.length; i++){
			Long currentDailyImpression = currentKpis[i].getDailyImpression();
			Long currentDailyClick = currentKpis[i].getDailyClick();
			Long currentDailyBudget = currentKpis[i].getDailyBudget();
			Long currentPeriod = currentKpis[i].getPeriod();
            Date currentDay = currentKpis[i].getDay();
			
            for(int j=0 ; j<kpis.length ;j++){
            	Long dailyImpression = kpis[j].getDailyImpression();
    			Long dailyClick = kpis[j].getDailyClick();
    			Long dailyBudget = kpis[j].getDailyBudget();
    			Long period = kpis[j].getPeriod();
                Date day = kpis[j].getDay();
                
                if(currentDay.equals(day)){
                	//比较时段
	                if (!ObjectUtils.equals(currentPeriod, period)) {
	                    result = true;
	                }
                	//比较日展现
        			if(!ObjectUtils.equals(currentDailyImpression, dailyImpression)){
        				result=true;
        			}
        			//比较日点击
        			if(!ObjectUtils.equals(currentDailyClick, dailyClick)){
        				result=true;
        			}
        			//比较日预算
        			if(!ObjectUtils.equals(currentDailyBudget, dailyBudget)){
        				result=true;
        			}
                }
            }
		}
		//比较C端频次控制类型frequencytype
		String currentObjectType = currentCampaignVO.getObjectType();
		String objectType = campaignVO.getObjectType();
		if(!ObjectUtils.equals(currentObjectType, objectType)){
			result=true;
		}
		//比较C端频次控制行为frequencyaction
		String currentFrequencyType = currentCampaignVO.getFrequencyType();
		String frequencyType = campaignVO.getFrequencyType();
		if(!ObjectUtils.equals(currentFrequencyType, frequencyType)){
			result=true;
		}
		//比较C端频次控制周期frequencyperiod
		String currentCycleType = currentCampaignVO.getCycleType();
		String cycleType = campaignVO.getCycleType();
		if(!ObjectUtils.equals(currentCycleType, cycleType)){
			result=true;
		}
		//比较C端频次控制数量frequencycount
		Integer currentFrequencyAmount = currentCampaignVO.getFrequencyAmount();
		Integer frequencyAmount = currentCampaignVO.getFrequencyAmount();
		if(!ObjectUtils.equals(currentFrequencyAmount, frequencyAmount)){
			result=true;
		}

		return result;
	}

    /**
     * 查询指定活动的状态。
     * @param campaignId    推广活动ID
     * @return  状态机枚举
     */
    public CampaignState getState(Integer campaignId)
    {
        CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);

        if (campaignId == null || campaign == null)
        {
            return null;
        }

        // 审核状态：1-未审核，2-已审核
        String auditStatus = campaign.getAuditStatus();

        if (StatusConstant.AUDIT_CAMPAIGN_WAIT_FOR_AUDIT.equals(auditStatus))
        {
            return CampaignState.WAIT_FOR_AUDIT;
        }
        else if (StatusConstant.AUDIT_CAMPAIGN_AUDIT_APPROVED.equals(auditStatus))
        {
            // 判断活动是否已结束
            boolean campaignFinished = isCampaignEndDate(campaignId);
            if (campaignFinished)
            {
                return CampaignState.FINISH;
            }

            // 判断活动此时是否到达投放周期，如果在投放周期返回true
            boolean campaignInPeriod = isCampaignPeriod(campaignId);
            if (!campaignInPeriod)
            {
                return CampaignState.OUT_OF_CYCLE;
            }

            // 判断活动的开关是否打开，如果开关是打开的返回true
            boolean campaignIsOpened = isOpenCampaign(campaignId);
            if (!campaignIsOpened)
            {
                return CampaignState.MANUAL_SUSPEND;
            }

            // 判断当前活动是否在投放定向时间内，如果在投放时段返回true
            boolean campaignInTargetTime = isOnTargetTime(campaignId);
            if (!campaignInTargetTime)
            {
                return CampaignState.OUT_OF_PHASE;
            }

            // 判断当前活动是否已完成全部KPI，如果KPI达到返回true
            boolean campaignKPIReached = isOverDailyKpi(campaignId);
            if (campaignKPIReached)
            {
                return CampaignState.OUT_OF_KPI;
            }

            // 判断当前活动是否已消耗光日成本，如果日成本已用完返回true
            boolean campaignOutOfCost = isOverDailyBudget(campaignId);
            if (campaignOutOfCost)
            {
                return CampaignState.OUT_OF_COST;
            }

            return CampaignState.LAUNCHING;
        }

        return null;
    }

    /**
     * 判断活动是否是已暂停状态（未到投放周期、手动暂停、不在投放时段、已到日KPI、已到日成本）。
     * @param state 活动的状态枚举
     * @return  如果状态概念上属于已暂停，即括中描述的几种状态，则返回true，否则返回false
     */
    public boolean isSuspended(CampaignState state)
    {
        boolean result = false;

        switch (state)
        {
            case OUT_OF_CYCLE:
            case MANUAL_SUSPEND:
            case OUT_OF_PHASE:
            case OUT_OF_KPI:
            case OUT_OF_COST:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    /**
     * 判断活动是否是投放中状态。
     * @param state 活动的状态枚举
     * @return  如果状态是LAUNCHING，则返回true，否则返回false
     */
    public boolean isLaunching(CampaignState state)
    {
        if (state == CampaignState.LAUNCHING)
        {
            return true;
        }
        return false;
    }

    /**
     * 判断活动是否是结束状态。
     * @param state 活动的状态枚举
     * @return  如果状态是FINISH，则返回true，否则返回false
     */
    public boolean isFinished(CampaignState state)
    {
        if (state == CampaignState.FINISH)
        {
            return true;
        }
        return false;
    }

    /**
     * 活动开关
     * @param campaignModel
     * @throws Exception
     */
	public void enableCampaign(CampaignModel campaignModel) throws Exception {
		Map<String,Object> map=new HashMap<>();
		map.put("campaignId", campaignModel.getId());
		
		if(StatusConstant.OFF_STATUS.equals(campaignModel.getEnable())){
			campaignMapper.updateByIdSelective(campaignModel);
			//获取该活动下的所有策略
			List<PolicyModel> policys = policyService.findAllPolicys(map);
			if(policys != null){
				for (PolicyModel policyModel : policys) {
					//将策略id从 策略ids中删除
					redisService.removePolicyId(policyModel.getId());
				}
			}
		}
		
		if(StatusConstant.ON_STATUS.equals(campaignModel.getEnable())){
			campaignMapper.updateByIdSelective(campaignModel);
			
			//活动是否在投放周期之内
			if(isCampaignPeriod(campaignModel.getId())){
				//获取该活动下的所有策略
				List<PolicyModel> policys = policyService.findAllPolicys(map);
				if(policys != null){
					for (PolicyModel policyModel : policys) {
						//策略投放开始时间在当前时间之前 && 策略投放结束时间在当前时间之后 && 策略打开 && 策略在定向时间 && 未到日KPI  && 未到日成本
						if(policyService.isPeriod(policyModel.getStartDate(), policyModel.getEndDate())
								&& policyService.isOpenSwitch(policyModel.getEnable())
								&& policyService.isOnTargetTime(policyModel.getId())
								&& !policyService.isOverDailyKpi(policyModel.getId())
								&& !policyService.isOverDailyBudget(policyModel.getId())){
							//将策略id写入策略ids中
							redisService.writePolicyId(policyModel.getId());
						}
					}
				}
			}
		}
	}

    public Integer updateauditStatus(String auditStatus,Integer id){
       return campaignMapper.updateauditStatus(auditStatus,id);
    }

    public PolicyVO convertVO(CampaignVO campaignModel,String realBid) {
        PolicyVO policyVO = new PolicyVO();
        policyVO.setCampaignId(campaignModel.getId());
        policyVO.setName(campaignModel.getName());
        policyVO.setStatus(StatusConstant.POLICY_SUSPENDED);
        policyVO.setEnable(StatusConstant.OFF_STATUS);
        policyVO.setTotalBudget(campaignModel.getTotalBudget());
        policyVO.setTotalImpression(campaignModel.getTotalImpression());
        policyVO.setTotalClick(campaignModel.getTotalClick());
        policyVO.setRealBid(Integer.valueOf(realBid));
        policyVO.setFrequencyType(campaignModel.getFrequencyType());
        policyVO.setObjectType(campaignModel.getObjectType());
        policyVO.setCycleType(campaignModel.getCycleType());
        policyVO.setFrequencyType(campaignModel.getFrequencyType());
        policyVO.setFrequencyAmount(campaignModel.getFrequencyAmount());
        policyVO.setStartDate(campaignModel.getStartDate());
        policyVO.setEndDate(campaignModel.getEndDate());
        policyVO.setPopulationRatio(campaignModel.getPopulationRatio());
        policyVO.setPopulationType(campaignModel.getPopulationType());
        policyVO.setScenePath(campaignModel.getScenePath());
        policyVO.setSceneRadius(campaignModel.getSceneRadius());
        policyVO.setSceneName(campaignModel.getSceneName());
        //匀速投放默认为是
        policyVO.setIsUniform(StatusConstant.POLICY_ISUNIFORM_TRUE);
        
        List<PolicyKpiVO> list = new ArrayList<>();
        PolicyKpiVO policyKpiVO = null;
        CampaignKpiVO[] kpi = campaignModel.getKpi();
        if(kpi!=null) {
            for (CampaignKpiVO campaignKpiModel : kpi) {
                policyKpiVO = new PolicyKpiVO();
                policyKpiVO.setDay(campaignKpiModel.getDay());
                policyKpiVO.setIsLock(campaignKpiModel.getIsLock());
                policyKpiVO.setPeriod(campaignKpiModel.getPeriod());
                policyKpiVO.setDailyBudget(campaignKpiModel.getDailyBudget());
                policyKpiVO.setDailyClick(campaignKpiModel.getDailyClick());
                policyKpiVO.setDailyImpression(campaignKpiModel.getDailyImpression());
                list.add(policyKpiVO);
            }
        }
        if (list.size() > 0) {
            PolicyKpiVO[] policyKpiVOs = new PolicyKpiVO[list.size()];
            policyVO.setKpi(list.toArray(policyKpiVOs));
        }
        CampaignTargetVO campaignTargetVO = campaignModel.getTargeting();
        if(campaignTargetVO!=null){
            policyVO.setTargeting( modelMapper.map(campaignTargetVO, PolicyTargetVO.class));
        }
        return policyVO;
    }
}
