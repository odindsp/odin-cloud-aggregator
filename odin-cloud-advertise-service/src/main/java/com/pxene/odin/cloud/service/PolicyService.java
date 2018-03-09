package com.pxene.odin.cloud.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.common.constant.TargetTypeConstant;
import com.pxene.odin.cloud.common.enumeration.CampaignState;
import com.pxene.odin.cloud.common.enumeration.CreativeState;
import com.pxene.odin.cloud.common.enumeration.PolicyState;
import com.pxene.odin.cloud.common.util.CamelCaseUtil;
import com.pxene.odin.cloud.common.util.DateUtils;
import com.pxene.odin.cloud.domain.model.CreativeModel;
import com.pxene.odin.cloud.domain.model.PolicyCreativeModel;
import com.pxene.odin.cloud.domain.model.PolicyKpiModel;
import com.pxene.odin.cloud.domain.model.PolicyModel;
import com.pxene.odin.cloud.domain.model.PolicyTargetingModel;
import com.pxene.odin.cloud.domain.vo.CampaignKpiVO;
import com.pxene.odin.cloud.domain.vo.CampaignTargetVO;
import com.pxene.odin.cloud.domain.vo.CampaignTargetingVO;
import com.pxene.odin.cloud.domain.vo.CampaignVO;
import com.pxene.odin.cloud.domain.vo.PolicyKpiVO;
import com.pxene.odin.cloud.domain.vo.PolicyTargetVO;
import com.pxene.odin.cloud.domain.vo.PolicyTargetingVO;
import com.pxene.odin.cloud.domain.vo.PolicyVO;
import com.pxene.odin.cloud.domain.vo.RegionVO;
import com.pxene.odin.cloud.domain.vo.RegionVO.City;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.exception.IllegalStatusException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyCreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyKpiMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyTargetingMapper;
import com.pxene.odin.cloud.web.api.BasicRegionClient;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pxene.odin.cloud.common.constant.StatusConstant.POLICY_BELONGTO_CAMPAIGN_SUSPENDED;
import static com.pxene.odin.cloud.common.constant.StatusConstant.START_DATE_AFTER;
import static com.pxene.odin.cloud.common.constant.StatusConstant.START_DATE_BEFORE;

@Service
@Transactional
public class PolicyService extends BaseService {

    @Autowired
    PolicyMapper policyMapper;
    @Autowired
    PolicyKpiMapper policyKpiMapper;
    @Autowired
    PolicyTargetingMapper policyTargetingMapper;
    @Autowired
    private CreativeMapper creativeMapper;
    @Autowired
    CampaignService campaignService;
    @Autowired
    private CreativeService creativeService;
    @Autowired
    private RedisService redisService;
    @Autowired
    PolicyCreativeMapper policyCreativeMapper;
    @Autowired
    BasicRegionClient basicRegionClient;

    public PolicyModel savePolicy(PolicyVO policyVO) throws Exception {
        CampaignVO campaignVO = campaignService.findById(policyVO.getCampaignId());
        if (campaignVO != null) {
        	//检查新建策略规则
        	checkSavePolicyRules(policyVO);
        	//检查策略规则
            checkPolicyRules(campaignVO, policyVO);
        }

        PolicyModel policyModel = modelMapper.map(policyVO, PolicyModel.class);
        //开关默认关闭
        policyModel.setEnable(StatusConstant.OFF_STATUS);
        //状态字段
        policyModel.setStatus("0");
        policyMapper.insert(policyModel);
        Integer policyId = policyModel.getId();

        PolicyKpiVO[] kpiVoArr = policyVO.getKpi();
        if (kpiVoArr != null) {
            for (PolicyKpiVO policyKpiVO : kpiVoArr) {
                PolicyKpiModel policyKpiModel = modelMapper.map(policyKpiVO, PolicyKpiModel.class);
                policyKpiModel.setPolicyId(policyId);
                policyKpiMapper.insert(policyKpiModel);
            }
        }

        PolicyTargetVO targeting = policyVO.getTargeting();
        if (targeting != null) {

            // 地域定向
            String[] regionIdArr = targeting.getRegion();
            if (regionIdArr != null) {
                for (String regionId : regionIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.REGION_TAGET);
                    targetingModel.setValue(regionId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 人群包定向
            String[] populationIdArr = targeting.getPopulation();
            if (populationIdArr != null) {
                for (String populationId : populationIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.POPULATION_TAGET);
                    targetingModel.setValue(populationId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 网络定向
            String[] networkArr = targeting.getNetwork();
            if (networkArr != null) {
                for (String networkId : networkArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.NETWORK_TAGET);
                    targetingModel.setValue(networkId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 运营商定向
            String[] carrierArr = targeting.getCarrier();
            if (carrierArr != null) {
                for (String carrierId : carrierArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.CARRIER_TAGET);
                    targetingModel.setValue(carrierId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 设备平台定向:
            String[] deviceArr = targeting.getDevice();
            if (deviceArr != null) {
                for (String deviceId : deviceArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.DEVICE_TAGET);
                    targetingModel.setValue(deviceId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 操作系统定向
            String[] osArr = targeting.getOs();
            if (osArr != null) {
                for (String osId : osArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.OS_TAGET);
                    targetingModel.setValue(osId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 手机品牌定向
            String[] brandArr = targeting.getBrand();
            if (brandArr != null) {
                for (String brandId : brandArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.BRAND_TAGET);
                    targetingModel.setValue(brandId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 渠道定向
            String[] adxArr = targeting.getAdx();
            if (adxArr != null) {
                for (String adxId : adxArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.ADX_TAGET);
                    targetingModel.setValue(adxId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 定价合同定向
            String[] contractArr = targeting.getContract();
            if (contractArr != null) {
                for (String contractId : contractArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.CONTRACT_TAGET);
                    targetingModel.setValue(contractId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 媒体类型定向
            PolicyTargetingVO appType = targeting.getAppType();
            if (appType != null && appType.getValue() != null) {
                String[] appTypeIdArr = appType.getValue();
                for (String appTypeId : appTypeIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.APP_TYPE_TAGET);
                    targetingModel.setIsInclude(appType.getIsInclude());
                    targetingModel.setValue(appTypeId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 媒体定向
            PolicyTargetingVO app = targeting.getApp();
            if (app != null && app.getValue() != null) {
                String[] appIdArr = app.getValue();
                for (String appId : appIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(policyId);
                    targetingModel.setType(TargetTypeConstant.APP_TAGET);
                    targetingModel.setIsInclude(app.getIsInclude());
                    targetingModel.setValue(appId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }
        }

        //策略的开始时间在当前时间之前
        if (StatusConstant.START_DATE_BEFORE.equals(checkStartDate(new Date(policyVO.getStartDate())))) {
            //将策略的基本信息写入redis : dsp_policy_info_(policyid)
            redisService.writePolicyInfo(policyId);
            //投放策略的定向信息写入redis : dsp_policyid_target_(policyid)
            redisService.writePolicyTarget(policyId);
            //投放策略引用的人群包写入redis : dsp _policy_audienceid_(policyid)
            redisService.writeWhiteBlack(policyId);
            //策略的投放控制策略写入redis : dsp_policy_control_(policyid)
            redisService.writePolicyControl(policyId);
            //dsp_fixprice_(adxcode)_ (dealid)ADX定价合同价格信息写入redis
            redisService.writeAdxContractBidInfo(policyId);
        }

        return policyModel;
    }

    private void checkSavePolicyRules(PolicyVO policyVO) {
    	//开始时间在当前时间之后 
        if(DateUtils.getCurrenDay().getTime()>policyVO.getStartDate().longValue()){
        	throw new DuplicateEntityException(PhrasesConstant.START_DATE_NOT_BEFORE);
        }
	}

	public void checkPolicyRules(CampaignVO campaignVO, PolicyVO policyVO) {

        // 投放周期校验
        long campaignStartDate = campaignVO.getStartDate();
        long campaignEndDate = campaignVO.getEndDate();
        long startDate = policyVO.getStartDate();
        long endDate = policyVO.getEndDate();
        
        if (startDate < campaignStartDate || endDate > campaignEndDate) {
            throw new DuplicateEntityException(PhrasesConstant.POLICY_DATE_MORE_THAN_CAMPAIGN);
        }
        // kpi校验
        Long campaignTotalImpression = campaignVO.getTotalImpression();
        Long campaignTotalClick = campaignVO.getTotalClick();
        Long campaignTotalBudget = campaignVO.getTotalBudget();
        Long totalImpression = policyVO.getTotalImpression();
        Long totalClick = policyVO.getTotalClick();
        Long totalBudget = policyVO.getTotalBudget();
		Long impressionSum = 0L;
		Long clickSum = 0L;
		Long budgeSum = 0L;
        
        CampaignKpiVO[] campaignKpi = campaignVO.getKpi();
        PolicyKpiVO[] policyKpi = policyVO.getKpi();
        
        if(totalImpression == null && totalClick == null){
			throw new DuplicateEntityException(PhrasesConstant.TOTAL_IMPRESSION_CLICK_NOT_NULL);
		}
    	if (policyKpi != null) {
			for (PolicyKpiVO policyKpiVO : policyKpi) {
				long period = policyKpiVO.getPeriod();
				Long dailyImpression = policyKpiVO.getDailyImpression();
				Long dailyClick = policyKpiVO.getDailyClick();
				Long dailyBudget = policyKpiVO.getDailyBudget();
				if ((period == 0 && dailyImpression != null && dailyImpression>0)
						|| (period == 0 && dailyClick != null && dailyClick>0)
						|| (period == 0 && dailyBudget != null && dailyBudget>0)) {

					SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
					String format = sdf.format(policyKpiVO.getDay());
					throw new DuplicateEntityException(format+PhrasesConstant.PERIOD_EMPTY_KPI_EXIST);
				}
				if ((period > 0 && dailyImpression == null )
						&& (period > 0 && dailyClick == null )) {

					SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
					String format = sdf.format(policyKpiVO.getDay());
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
		String scenePath = policyVO.getScenePath();
		String sceneRadius = policyVO.getSceneRadius();
		if(scenePath == null && sceneRadius != null){
			throw new DuplicateEntityException(PhrasesConstant.SCENE_NO_PATH);
		}
		if(scenePath != null && sceneRadius == null){
			throw new DuplicateEntityException(PhrasesConstant.SCENE_NO_RADIUS);
		}
        
        //====策略活动规则校验=========
        
        if ((campaignTotalImpression == null && totalImpression != null) || (campaignTotalClick == null && totalClick != null)) {
            throw new DuplicateEntityException(PhrasesConstant.POLICY_TOTAL_KPI_MORE_THEN_CAMPAIGN);
        }
        
        if((campaignTotalBudget == null) && totalBudget != null){
        	throw new DuplicateEntityException(PhrasesConstant.POLICY_TOTAL_BUDGET_MORE_THEN_CAMPAIGN);
        }
        
        if((campaignTotalImpression != null && totalImpression != null)){
        	if(totalImpression.longValue() > campaignTotalImpression.longValue()){
        		throw new DuplicateEntityException(PhrasesConstant.POLICY_TOTAL_KPI_MORE_THEN_CAMPAIGN);
        	}
        }
        if(campaignTotalClick != null && totalClick != null){
        	if(totalClick.longValue() > campaignTotalClick.longValue()){
        		 throw new DuplicateEntityException(PhrasesConstant.POLICY_TOTAL_KPI_MORE_THEN_CAMPAIGN);
        	}
        }
        if(campaignTotalBudget != null && totalBudget != null){
        	if(totalBudget.longValue() > campaignTotalBudget.longValue()){
        		throw new DuplicateEntityException(PhrasesConstant.POLICY_TOTAL_BUDGET_MORE_THEN_CAMPAIGN);
        	}
        }

        for (int i = 0; i < campaignKpi.length; i++) {
            Long dailyImpression = campaignKpi[i].getDailyImpression();
            Long dailyClick = campaignKpi[i].getDailyClick();
            Long dailyBudget = campaignKpi[i].getDailyBudget();
            Date day = campaignKpi[i].getDay();
            
            for (int j = 0; j< policyKpi.length ; j++) {
            	if(policyKpi[j].getDay().equals(day)){
            		
            		 Long policyDailyImpression = policyKpi[j].getDailyImpression();
                     Long policyDailyClick = policyKpi[j].getDailyClick();
                     Long policyDailyBudget = policyKpi[j].getDailyBudget();
                     
                     SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
                	 String format = sdf.format(day);
                     
                     if ((dailyImpression == null && policyDailyImpression != null) || (dailyClick == null
                         && policyDailyClick != null) ) {
                         throw new DuplicateEntityException(format+PhrasesConstant.POLICY_KPI_MORE_THEN_CAMPAIGN);
                     }
                     if((dailyBudget == null && policyDailyBudget != null)){
                    	 throw new DuplicateEntityException(format+PhrasesConstant.POLICY_BUDGET_MORE_THEN_CAMPAIGN);
                     }
                     if ((dailyImpression != null && policyDailyImpression != null)) {
                         if (dailyImpression.longValue() < policyDailyImpression.longValue()) {
                             throw new DuplicateEntityException(format+PhrasesConstant.POLICY_KPI_MORE_THEN_CAMPAIGN);
                         }
                     }
                     if(dailyClick != null&& policyDailyClick != null){
                    	 if(dailyClick.longValue() < policyDailyClick.longValue()){
                    		 throw new DuplicateEntityException(format+PhrasesConstant.POLICY_KPI_MORE_THEN_CAMPAIGN);
                    	 }
                     }
                     if((dailyBudget != null && policyDailyBudget != null)){
                    	 if(dailyBudget.longValue() < policyDailyBudget.longValue()){
                    		 throw new DuplicateEntityException(format+PhrasesConstant.POLICY_BUDGET_MORE_THEN_CAMPAIGN);
                    	 }
                     }
                     //对时段校验
                     Long campaignPeriod = campaignKpi[i].getPeriod();
                     Long policyPeriod = policyKpi[j].getPeriod();
                     Long result = campaignPeriod | policyPeriod;
                    
                     if(campaignPeriod==0 && !campaignPeriod.equals(policyPeriod)){
                    	 
                    	 throw new DuplicateEntityException(format+PhrasesConstant.POLICY_NOT_ADVERTISE);
                     }
                     if (!campaignPeriod.equals(result)) {
                     		throw new DuplicateEntityException(format+PhrasesConstant.POLICY_PERIOD_MORE_THEN_CAMPAIGN);
                     }
            	}
			}
         }

        CampaignTargetVO campaignTargeting = campaignVO.getTargeting();
        PolicyTargetVO policyTargeting = policyVO.getTargeting();
        //地域校验
        String[] campaignRegion = campaignTargeting.getRegion();
        String[] policyRegion = policyTargeting.getRegion();
        List<String> campaignRegionList = new ArrayList<>(Arrays.asList(campaignRegion));
        List<String> policyRegionList = Arrays.asList(policyRegion);
        
        //前端地域选择全省只传省ID,获取省和市信息
        String listRegions = basicRegionClient.listRegions();
        JsonArray jsonArray = new JsonParser().parse(listRegions).getAsJsonObject().getAsJsonArray("items");
        List<RegionVO> regions=new Gson().fromJson(jsonArray, new TypeToken<List<RegionVO>>() {
		}.getType());
        
        for (String regionId : campaignRegion) {
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
						campaignRegionList.addAll(cityIdList);
					}
				}
			}
		}
        if(campaignRegionList.size() != 0 && policyRegionList.size() ==0){
        	 throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        
        if (campaignRegionList.size() != 0 && !campaignRegionList.containsAll(policyRegionList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //人群包定向
        String[] campaignPopulation = campaignTargeting.getPopulation();
        String[] policyPopulation = policyTargeting.getPopulation();
        List<String> campaignPopulationList = Arrays.asList(campaignPopulation);
        List<String> policyPopulationList = Arrays.asList(policyPopulation);
        if(campaignPopulationList.size() !=0 && policyPopulationList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignPopulationList.size() != 0 && !campaignPopulationList.containsAll(policyPopulationList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //网络定向
        String[] campaignNetwork = campaignTargeting.getNetwork();
        String[] policyNetwork = policyTargeting.getNetwork();
        List<String> campaignNetworkList = Arrays.asList(campaignNetwork);
        List<String> policyNetworkList = Arrays.asList(policyNetwork);
        if(campaignNetworkList.size() !=0 && policyNetworkList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignNetworkList.size() != 0 && !campaignNetworkList.containsAll(policyNetworkList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //运营商定向
        String[] campaignCarrier = campaignTargeting.getCarrier();
        String[] policyCarrier = policyTargeting.getCarrier();
        List<String> campaignCarrierList = Arrays.asList(campaignCarrier);
        List<String> policyCarrierList = Arrays.asList(policyCarrier);
        if(campaignCarrierList.size() !=0 && policyCarrierList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignCarrierList.size() != 0 && !campaignCarrierList.containsAll(policyCarrierList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //设备平台定向
        String[] campaignDevice = campaignTargeting.getDevice();
        String[] policyDevice = policyTargeting.getDevice();
        List<String> campaignDeviceList = Arrays.asList(campaignDevice);
        List<String> policyDeviceList = Arrays.asList(policyDevice);
        if(campaignDeviceList.size() !=0 && policyDeviceList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignDeviceList.size() != 0 && !campaignDeviceList.containsAll(policyDeviceList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //系统定向
        String[] campaignOs = campaignTargeting.getOs();
        String[] policyOs = policyTargeting.getOs();
        List<String> campaignOsList = Arrays.asList(campaignOs);
        List<String> policyOsList = Arrays.asList(policyOs);
        if(campaignOsList.size() !=0 && policyOsList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignOsList.size() != 0 && !campaignOsList.containsAll(policyOsList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //品牌定向
        String[] campaignBrand = campaignTargeting.getBrand();
        String[] policyBrand = policyTargeting.getBrand();
        List<String> campaignBrandList = Arrays.asList(campaignBrand);
        List<String> policyBrandList = Arrays.asList(policyBrand);
        if(campaignBrandList.size() !=0 && policyBrandList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignBrandList.size() != 0 && !campaignBrandList.containsAll(policyBrandList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //场景定向 半径校验
        String campaignRadius = campaignVO.getSceneRadius();
        String policyRadius = policyVO.getSceneRadius();
        if (campaignRadius != null && policyRadius == null) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }

        if (campaignRadius != null && Integer.parseInt(policyRadius) < Integer.parseInt(campaignRadius)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //场景定向 上传文件校验
        String campaignScenePath = campaignVO.getScenePath();
        String policyScenePath = policyVO.getScenePath();
        if (campaignScenePath != null && !StringUtils.equals(campaignScenePath, policyScenePath)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }

        //渠道定向
        String[] campaignAdx = campaignTargeting.getAdx();
        String[] policyAdx = policyTargeting.getAdx();
        List<String> campaignAdxList = Arrays.asList(campaignAdx);
        List<String> policyAdxList = Arrays.asList(policyAdx);
        if(campaignAdxList.size() !=0 && policyAdxList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignAdxList.size() != 0 && !campaignAdxList.containsAll(policyAdxList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //定价合同定向
        String[] campaignContract = campaignTargeting.getContract();
        String[] policyContract = policyTargeting.getContract();
        List<String> campaignContractList = Arrays.asList(campaignContract);
        List<String> policyContractList = Arrays.asList(policyContract);
        if(campaignContractList.size() !=0 && policyContractList.size() ==0){
        	throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        if (campaignContractList.size() != 0 && !campaignContractList.containsAll(policyContractList)) {
            throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
        }
        //媒体分类定向
        CampaignTargetingVO campaignAppType = campaignTargeting.getAppType();
        PolicyTargetingVO policyAppType = policyTargeting.getAppType();
        String campaignIsInclude = campaignAppType == null ? null : campaignAppType.getIsInclude();
        String policyIsInclude = policyAppType == null ? null : policyAppType.getIsInclude();
        
        

        if (StatusConstant.INCLUDE_TYPE_VALUE.equals(campaignIsInclude)) {
            if (!StatusConstant.INCLUDE_TYPE_VALUE.equals(policyIsInclude)) {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }

            List<String> campaignAppTypeList = Arrays.asList(campaignAppType.getValue());
            List<String> policyAppTypeList = Arrays.asList(policyAppType.getValue());
            if (campaignAppTypeList.size() != 0 && !campaignAppTypeList.containsAll(policyAppTypeList)) {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
        }

        if (StatusConstant.EXCLUDE_TYPE_VALUE.equals(campaignIsInclude)) {
            if (!StatusConstant.EXCLUDE_TYPE_VALUE.equals(policyIsInclude)) {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }

            List<String> campaignAppTypeList = Arrays.asList(campaignAppType.getValue());
            List<String> policyAppTypeList = Arrays.asList(policyAppType.getValue());
            if (campaignAppTypeList.size() != 0 && campaignAppTypeList.containsAll(policyAppTypeList)) {
                if (campaignAppTypeList.size() != policyAppTypeList.size()) {
                    throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
                }
            }
        }
        //媒体名称定向
        CampaignTargetingVO campaignApp = campaignTargeting.getApp();
        PolicyTargetingVO policyApp = policyTargeting.getApp();
        campaignIsInclude = campaignApp == null ? null : campaignApp.getIsInclude();
        policyIsInclude = policyApp == null ? null : policyApp.getIsInclude();

        if (StatusConstant.INCLUDE_TYPE_VALUE.equals(campaignIsInclude)) {
            if (!StatusConstant.INCLUDE_TYPE_VALUE.equals(policyIsInclude)) {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }

            List<String> campaignAppList = Arrays.asList(campaignApp.getValue());
            List<String> policyAppList = Arrays.asList(policyApp.getValue());
            if (campaignAppList.size() != 0 && !campaignAppList.containsAll(policyAppList)) {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }
        }

        if (StatusConstant.EXCLUDE_TYPE_VALUE.equals(campaignIsInclude)) {
            if (!StatusConstant.EXCLUDE_TYPE_VALUE.equals(policyIsInclude)) {
                throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
            }

            List<String> campaignAppList = Arrays.asList(campaignApp.getValue());
            List<String> policyAppList = Arrays.asList(policyApp.getValue());
            if (campaignAppList.size() != 0 && campaignAppList.containsAll(policyAppList)) {
                if (campaignAppList.size() != policyAppList.size()) {
                    throw new DuplicateEntityException(PhrasesConstant.PARAM_RULE_ERROR);
                }
            }
        }

    }

    public PolicyModel selectPolicyById(Integer policyId) {
        return policyMapper.selectByPrimaryKey(policyId);
    }

    public PolicyVO findById(Integer id) {

        PolicyModel policyModel = selectPolicyById(id);
        if (policyModel == null) {
            return null;
        }

        PolicyVO policyVO = modelMapper.map(policyModel, PolicyVO.class);

        // kpi
        List<PolicyKpiModel> kpiModelList = policyKpiMapper.selectByPolicyId(policyModel.getId());
        if (kpiModelList != null) {
            int len = kpiModelList.size();
            PolicyKpiVO[] kpiVOArr = new PolicyKpiVO[len];
            for (int i = 0; i < len; i++) {
                PolicyKpiVO policyKpiVO = modelMapper.map(kpiModelList.get(i), PolicyKpiVO.class);
                kpiVOArr[i] = policyKpiVO;
            }
            policyVO.setKpi(kpiVOArr);
        }

        PolicyTargetVO targetVO = new PolicyTargetVO();
        policyVO.setTargeting(targetVO);

        // adx定向
        List<PolicyTargetingModel> adxList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.ADX_TAGET);
        if (adxList != null) {
            int adxLen = adxList.size();
            String[] adx = new String[adxLen];
            for (int i = 0; i < adxLen; i++) {
                adx[i] = adxList.get(i).getValue();
            }
            targetVO.setAdx(adx);
        }
        // 定价合同定向
        List<PolicyTargetingModel> contractList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.CONTRACT_TAGET);
        if (contractList != null) {
            int contractLen = contractList.size();
            String[] contract = new String[contractLen];
            for (int i = 0; i < contractLen; i++) {
                contract[i] = contractList.get(i).getValue();
            }
            targetVO.setContract(contract);
        }
        // 品牌定向
        List<PolicyTargetingModel> brandList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.BRAND_TAGET);
        if (brandList != null) {
            int brandLen = brandList.size();
            String[] brand = new String[brandLen];
            for (int i = 0; i < brandLen; i++) {
                brand[i] = brandList.get(i).getValue();
            }
            targetVO.setBrand(brand);
        }
        // 运营商定向
        List<PolicyTargetingModel> carrierList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.CARRIER_TAGET);
        if (carrierList != null) {
            int carrierLen = carrierList.size();
            String[] carrier = new String[carrierLen];
            for (int i = 0; i < carrierLen; i++) {
                carrier[i] = carrierList.get(i).getValue();
            }
            targetVO.setCarrier(carrier);
        }
        // 设备定向
        List<PolicyTargetingModel> deviceList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.DEVICE_TAGET);
        if (deviceList != null) {
            int deviceLen = deviceList.size();
            String[] device = new String[deviceLen];
            for (int i = 0; i < deviceLen; i++) {
                device[i] = deviceList.get(i).getValue();
            }
            targetVO.setDevice(device);
        }
        // 网络定向
        List<PolicyTargetingModel> networkList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.NETWORK_TAGET);
        if (networkList != null) {
            int networkLen = networkList.size();
            String[] network = new String[networkLen];
            for (int i = 0; i < networkLen; i++) {
                network[i] = networkList.get(i).getValue();
            }
            targetVO.setNetwork(network);
        }
        // 系统定向
        List<PolicyTargetingModel> osList = policyTargetingMapper.selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.OS_TAGET);
        if (osList != null) {
            int osLen = osList.size();
            String[] os = new String[osLen];
            for (int i = 0; i < osLen; i++) {
                os[i] = osList.get(i).getValue();
            }
            targetVO.setOs(os);
        }
        // 人群包定向
        List<PolicyTargetingModel> populationList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.POPULATION_TAGET);
        if (populationList != null) {
            int populationLen = populationList.size();
            String[] population = new String[populationLen];
            for (int i = 0; i < populationLen; i++) {
                population[i] = populationList.get(i).getValue();
            }
            targetVO.setPopulation(population);
        }
        // 地域定向
        List<PolicyTargetingModel> regionList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.REGION_TAGET);
        if (regionList != null) {
            int regionLen = regionList.size();
            String[] region = new String[regionLen];
            for (int i = 0; i < regionLen; i++) {
                region[i] = regionList.get(i).getValue();
            }
            targetVO.setRegion(region);
        }
        // 媒体分类
        List<PolicyTargetingModel> appTypeList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.APP_TYPE_TAGET);
        if (appTypeList != null && !appTypeList.isEmpty()) {
            int appTypeLen = appTypeList.size();
            String[] appType = new String[appTypeLen];
            for (int i = 0; i < appTypeLen; i++) {
                appType[i] = appTypeList.get(i).getValue();
            }
            PolicyTargetingVO policyTargetingVO = new PolicyTargetingVO();
            policyTargetingVO.setIsInclude(appTypeList.get(0).getIsInclude());
            policyTargetingVO.setValue(appType);
            targetVO.setAppType(policyTargetingVO);
        }else{
        	PolicyTargetingVO policyTargetingVO = new PolicyTargetingVO();
        	targetVO.setAppType(policyTargetingVO);
        }
        // 媒体名称
        List<PolicyTargetingModel> appList = policyTargetingMapper
            .selectByPolicyIdAndType(policyModel.getId(), TargetTypeConstant.APP_TAGET);
        if (appList != null && !appList.isEmpty()) {
            int appLen = appList.size();
            String[] app = new String[appLen];
            for (int i = 0; i < appLen; i++) {
                app[i] = appList.get(i).getValue();
            }
            PolicyTargetingVO policyTargetingVO = new PolicyTargetingVO();
            policyTargetingVO.setIsInclude(appList.get(0).getIsInclude());
            policyTargetingVO.setValue(app);
            targetVO.setApp(policyTargetingVO);
        }else{
        	PolicyTargetingVO policyTargetingVO = new PolicyTargetingVO();
        	targetVO.setApp(policyTargetingVO);
        }
        //查询创意数
        //TODO

        //查询数据信息
        //TODO

        // 今日推广活动数据
        // TODO

        // 累计推广活动数据
        // TODO

        return policyVO;
    }

    public void updatePolicy(Integer id, PolicyVO currentPolicyVO, PolicyVO policyVO) throws Exception {
        PolicyModel policyModel = modelMapper.map(policyVO, PolicyModel.class);
        policyModel.setId(id);
        policyMapper.updateByIdSelective(policyModel);

        PolicyKpiVO[] kpiVoArr = policyVO.getKpi();
        if (kpiVoArr != null) {
            policyKpiMapper.deleteByPolicyId(id);
            for (PolicyKpiVO policyKpiVO : kpiVoArr) {
                PolicyKpiModel policyKpiModel = modelMapper.map(policyKpiVO, PolicyKpiModel.class);
                policyKpiModel.setPolicyId(id);
                policyKpiMapper.insert(policyKpiModel);
            }
        }

        PolicyTargetVO targeting = policyVO.getTargeting();
        if (targeting != null) {

            // 地域定向
            String[] regionIdArr = targeting.getRegion();
            if (regionIdArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.REGION_TAGET);
                for (String regionId : regionIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.REGION_TAGET);
                    targetingModel.setValue(regionId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 人群包定向
            String[] populationIdArr = targeting.getPopulation();
            if (populationIdArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.POPULATION_TAGET);
                for (String populationId : populationIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.POPULATION_TAGET);
                    targetingModel.setValue(populationId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 网络定向
            String[] networkArr = targeting.getNetwork();
            if (networkArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.NETWORK_TAGET);
                for (String networkId : networkArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.NETWORK_TAGET);
                    targetingModel.setValue(networkId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 运营商定向
            String[] carrierArr = targeting.getCarrier();
            if (carrierArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.CARRIER_TAGET);
                for (String carrierId : carrierArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.CARRIER_TAGET);
                    targetingModel.setValue(carrierId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 设备平台定向:
            String[] deviceArr = targeting.getDevice();
            if (deviceArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.DEVICE_TAGET);
                for (String deviceId : deviceArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.DEVICE_TAGET);
                    targetingModel.setValue(deviceId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 操作系统定向
            String[] osArr = targeting.getOs();
            if (osArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.OS_TAGET);
                for (String osId : osArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.OS_TAGET);
                    targetingModel.setValue(osId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 手机品牌定向
            String[] brandArr = targeting.getBrand();
            if (brandArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.BRAND_TAGET);
                for (String brandId : brandArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.BRAND_TAGET);
                    targetingModel.setValue(brandId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 渠道定向
            String[] adxArr = targeting.getAdx();
            if (adxArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.ADX_TAGET);
                for (String adxId : adxArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.ADX_TAGET);
                    targetingModel.setValue(adxId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 定价合同定向
            String[] contractArr = targeting.getContract();
            if (contractArr != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.CONTRACT_TAGET);
                for (String contractId : contractArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.CONTRACT_TAGET);
                    targetingModel.setValue(contractId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 媒体类型定向
            PolicyTargetingVO appType = targeting.getAppType();
            if (appType != null && appType.getValue() != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.APP_TYPE_TAGET);
                String[] appTypeIdArr = appType.getValue();
                for (String appTypeId : appTypeIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.APP_TYPE_TAGET);
                    targetingModel.setIsInclude(appType.getIsInclude());
                    targetingModel.setValue(appTypeId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }

            // 媒体定向
            PolicyTargetingVO app = targeting.getApp();
            if (app != null && app.getValue() != null) {
                policyTargetingMapper.deleteByPolicyIdAndType(id, TargetTypeConstant.APP_TAGET);
                String[] appIdArr = app.getValue();
                for (String appId : appIdArr) {
                    PolicyTargetingModel targetingModel = new PolicyTargetingModel();
                    targetingModel.setPolicyId(id);
                    targetingModel.setType(TargetTypeConstant.APP_TAGET);
                    targetingModel.setIsInclude(app.getIsInclude());
                    targetingModel.setValue(appId);
                    policyTargetingMapper.insert(targetingModel);
                }
            }
        }

        //未到策略投放周期
        if (StatusConstant.START_DATE_AFTER.equals(checkStartDate(new Date(currentPolicyVO.getStartDate())))) {
            //编辑除周期外策略信息,不操作redis
            //编辑策略投放周期
            //if (isUpdatePolicyDate(currentPolicyVO, policyVO)) {
                //编辑后策略投放周期开始时间为今天
                if (isTodayStart(new Date(policyVO.getStartDate()))) {
                    //将策略的基本信息写入redis : dsp_policy_info_(policyid)
                    redisService.writePolicyInfo(id);
                    //投放策略的定向信息写入redis : dsp_policyid_target_(policyid)
                    redisService.writePolicyTarget(id);
                    //投放策略引用的人群包写入redis : dsp _policy_audienceid_(policyid)
                    redisService.writeWhiteBlack(id);
                    //策略的投放控制策略写入redis : dsp_policy_control_(policyid)
                    redisService.writePolicyControl(id);
                    //dsp_fixprice_(adxcode)_ (dealid)ADX定价合同价格信息等写入redis中
                    redisService.writeAdxContractBidInfo(id);

                    //若策略下有创意
                    if (isHaveCreative(id)) {
                        //根据策略id查询策略和创意的关联信息
                        List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(id);
                        if (policyCreatives != null) {
                            for (PolicyCreativeModel policyCreativeModel : policyCreatives) {
                                Integer creativeId = policyCreativeModel.getCreativeId();
                                Integer policyCreativeId = policyCreativeModel.getId();
                                //单个创意的详细信息写入redis : dsp_mapid_(mapid)
                                redisService.writeCreativeInfo(policyCreativeId);
                                //创意的出价信息写入redis : dsp_mapid_bid_(mapid)
                                redisService.writeCreateBid(policyCreativeId);
                                //策略下创意打开 && 物料包下创意打开 && 物料包下创意审核通过
                                if (creativeService.isOpenPolicyCreative(id) && creativeService.isOpenPackageCreative(creativeId)
                                    && creativeService.isPassAudit(creativeId)) {
                                    //投放策略下所有的创意ID写入redis : dsp_policy_mapids_(policyid)
                                    redisService.writeCreativeIds(id);

                                }
                            }
                        }
                        //在定向时间段内 &&  策略开启 && 活动开启 && 未到日KPI && 未到日成本
                        if (isOnTargetTime(id) && isOpenPolicy(id) && campaignService.isOpenCampaign(policyVO.getCampaignId())
                            && !isOverDailyKpi(id) && !isOverDailyBudget(id)) {
                            //将策略id写入策略ids中:所有的投放策略ID即dsp_policyids
                            redisService.writePolicyId(id);
                        }
                    }
                }
            //}
        }

        //在策略投放周期
        if (isPeriod(new Date(currentPolicyVO.getStartDate()), new Date(currentPolicyVO.getEndDate()))) {
            //编辑除周期外策略信息
            //if (isUpdatePolicyControl(currentPolicyVO, policyVO)) {
                //重新写入策略的投放控制策略 dsp_policy_control_(policyid)
                redisService.writePolicyControl(id);
                //重新写入创意基本信息
                if (isHaveCreative(id)) {
                    //根据策略id查询策略和创意的关联信息
                    List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(id);
                    if (policyCreatives != null) {
                        for (PolicyCreativeModel policyCreativeModel : policyCreatives) {
                            //重新写入创意基本信息
                            redisService.writeCreativeInfo(policyCreativeModel.getId());
                        }
                    }
                }
                //重新写入策略定向
                redisService.writePolicyTarget(id);
                //重新写入策略基本信息
                redisService.writePolicyInfo(id);
                //修改时间定向
                if (isUpdateTargetTime(currentPolicyVO, policyVO)) {
                    //活动开启 && 策略开启 && 在定向时间 && 未到日KPI  && 未到日成本
                    if (campaignService.isOpenCampaign(policyModel.getCampaignId()) && isOpenPolicy(id) && isOnTargetTime(id)
                        && !isOverDailyKpi(id) && !isOverDailyBudget(id)) {
                        //将策略id写入策略ids中:所有的投放策略ID即dsp_policyids
                        redisService.writePolicyId(id);
                    }
                    //当前策略不在投放定向时间里
                    if (!isOnTargetTime(id)) {
                        redisService.removePolicyId(id);
                    }
                }
                //dsp_fixprice_(adxcode)_ (dealid)ADX定价合同价格信息等写入redis中
                redisService.writeAdxContractBidInfo(id);
            //}

            //编辑策略投放周期,不操作redis
        }

        //策略投放周期已结束
        if (isEndDate(new Date(currentPolicyVO.getEndDate()))) {
            //编辑除周期外策略信息,不操作redis
            //编辑策略投放周期
            //if (isUpdatePolicyDate(currentPolicyVO, policyVO)) {
                //从投放结束调到投放周期中
                if (isPolicyPeriod(id)) {
                    //将策略的基本信息写入redis : dsp_policy_info_(policyid)
                    redisService.writePolicyInfo(id);
                    //投放策略的定向信息写入redis : dsp_policyid_target_(policyid)
                    redisService.writePolicyTarget(id);
                    //投放策略引用的人群包写入redis : dsp _policy_audienceid_(policyid)
                    redisService.writeWhiteBlack(id);
                    //策略的投放控制策略写入redis : dsp_policy_control_(policyid)
                    redisService.writePolicyControl(id);
                    //dsp_fixprice_(adxcode)_ (dealid)ADX定价合同价格信息等写入redis中
                    redisService.writeAdxContractBidInfo(id);
                    //策略下有创意
                    if (isHaveCreative(id)) {
                        //根据策略id查询策略和创意的关联信息
                        List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(id);
                        for (PolicyCreativeModel policyCreativeModel : policyCreatives) {
                            Integer policyCreativeId = policyCreativeModel.getId();
                            Integer creativeId = policyCreativeModel.getCreativeId();

                            //单个创意的详细信息写入redis : dsp_mapid_(mapid)
                            redisService.writeCreativeInfo(policyCreativeId);
                            //创意的出价信息写入redis : dsp_mapid_bid_(mapid)
                            redisService.writeCreateBid(policyCreativeId);
                            //策略下创意打开 && 物料包下创意打开 && 物料包下创意审核通过
                            if (creativeService.isOpenPolicyCreative(id) && creativeService.isOpenPackageCreative(creativeId)
                                && creativeService.isPassAudit(creativeId)) {
                                //投放策略下所有的创意ID写入redis : dsp_policy_mapids_(policyid)
                                redisService.writeCreativeIds(id);
                            }
                        }
                    }

                    //活动开启 && 策略开启 && 在定向时间 && 未到日KPI  && 未到日成本
                    if (campaignService.isOpenCampaign(policyModel.getCampaignId()) && isOpenPolicy(id) && isOnTargetTime(id)
                        && !isOverDailyKpi(id) && !isOverDailyBudget(id)) {
                        //将策略id写入策略ids中:所有的投放策略ID即dsp_policyids
                        redisService.writePolicyId(id);
                    }
                }
           // }
        }
    }

    public List<PolicyModel> findAllPolicys(Map<String, Object> map) {
        return policyMapper.findAllPolicys(map);
    }

    public List<PolicyVO> listAllPolicys(Integer campaignId, Long adStartDate,Long adEndDate, Integer id, String name, String status, Long startDate,
        Long endDate, String sortKey, String sortType) {
        Map<String, Object> map = new HashMap<>();
        map.put("campaignId", campaignId);
        String adStartDateFormat = adStartDate==null ? null : new SimpleDateFormat("yyyy-MM-dd").format(new Date(adStartDate));
        String adEndDateFormat = adEndDate==null ? null : new SimpleDateFormat("yyyy-MM-dd").format(new Date(adEndDate));
        map.put("adStartDate", adStartDateFormat);
        map.put("adEndDate", adEndDateFormat);
        map.put("id", id);
        map.put("name", name);
        //map.put("status", status);

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

        List<PolicyModel> findAllPolicys = findAllPolicys(map);

        List<PolicyVO> policyVOList = new ArrayList<>();
        for (PolicyModel policyModel : findAllPolicys) {
            PolicyVO policyVO = modelMapper.map(policyModel, PolicyVO.class);
            //查询策略状态
            PolicyState state = getState(policyModel.getId());
            policyVO.setStatus(state.getCode());
            //查询创意数
            Integer creativeCount = policyCreativeMapper.selectCreativeCountByPolicyId(policyModel.getId());
            policyVO.setCreativeAmount(creativeCount);
            //查询数据信息
            //TODO

            // 今日推广活动数据
            // TODO

            // 累计推广活动数据
            // TODO
            if(status!=null){
            	//已暂停包括已暂停-未到投放周期、已暂停、已暂停-不在投放时段、已暂停-推广活动已暂停、已暂停-已到日KPI、已暂停-已到日成本
            	if(StatusConstant.POLICY_SUSPENDED.equals(status)){
					if(StatusConstant.POLICY_SUSPENDED.equals(state.getCode())
							||StatusConstant.POLICY_OUT_OF_CYCLE.equals(state.getCode())
							||StatusConstant.POLICY_OUT_OF_PHASE.equals(state.getCode())
							||StatusConstant.POLICY_BELONGTO_CAMPAIGN_SUSPENDED.equals(state.getCode())
							||StatusConstant.POLICY_OUT_OF_KPI.equals(state.getCode())
							||StatusConstant.POLICY_OUT_OF_COST.equals(state.getCode())){
						policyVOList.add(policyVO);
					}
				}else if(status.equals(state.getCode())){
            		policyVOList.add(policyVO);
            	}
            }else{
            	policyVOList.add(policyVO);
            }
        }
        return policyVOList;
    }

    public void checkUpdatePolicyRules(PolicyVO currentPolicyVO, PolicyVO policyVO) {

        long currentStartDate = currentPolicyVO.getStartDate();
        long currentEndDate = currentPolicyVO.getEndDate();
        long startDate = policyVO.getStartDate();
        long endDate = policyVO.getEndDate();
        long time = DateUtils.getCurrenDay().getTime();
        
        //未开始投放前
        if (time < currentStartDate) {
            //在推广活动投放周期范围内
//			if(startDate != currentStartDate && startDate>time){
//				throw new DuplicateEntityException(PhrasesConstant.PARAM_ERROR);
//			}
        }
        //投放中
        if (currentStartDate <= time && time <= currentEndDate) {
        	if(startDate != currentStartDate){
        		throw new DuplicateEntityException(PhrasesConstant.POLICY_START_DATE_NOT_UPDATE);
        	}
            if ( (currentEndDate != endDate && endDate < time)) {
                throw new DuplicateEntityException(PhrasesConstant.POLICY_END_DATE_NOT_BEFORE_TODAY);
            }
        }
        //投放结束后
        if (currentEndDate < time) {
        	if(startDate != currentStartDate){
        		 throw new DuplicateEntityException(PhrasesConstant.POLICY_START_DATE_NOT_UPDATE);
        	}
            if (currentEndDate != endDate && endDate < time) {
                throw new DuplicateEntityException(PhrasesConstant.POLICY_END_DATE_NOT_BEFORE_TODAY);
            }
        }
    }

	public PolicyModel findByNameAndCampaignId(String name, Integer campaignId) {

        List<PolicyModel> list = policyMapper.selectByNameAndCampaignId(name, campaignId);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public boolean isNameExist(PolicyVO policyVO) {
        return findByNameAndCampaignId(policyVO.getName(), policyVO.getCampaignId()) != null;
    }

    public boolean isUpdateNameExist(PolicyVO policyVO) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", policyVO.getId());
        map.put("name", policyVO.getName());
        map.put("campaignId", policyVO.getCampaignId());
        List<PolicyModel> policyModels = policyMapper.selectByNotId(map);

        return policyModels.size() != 0;
    }

    /**
     * 判断当前策略是否在投放定向时间内
     */
    public boolean isOnTargetTime(Integer policyId) {
        // 当前天
        Date day = DateUtils.getCurrenDay();
        // 查询今天的时间定向
        PolicyKpiModel policyKpi = policyKpiMapper.selectByPrimaryKey(policyId, day);
        if (policyKpi != null) {
            Integer period = policyKpi.getPeriod().intValue();
            // 将十段转成二进制
            String periods = Integer.toBinaryString(period);
            // 获取二进制的长度
            int length = periods.length();
            // 24位二进制
            for (int i = 0; i < 24 - length; i++) {
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
     * 判断日成本是否已到上限
     *
     * @param policyId 策略id
     */
    public boolean isOverDailyBudget(Integer policyId) {
//        // 当前天
//        Date day = DateUtils.getCurrenDay();
//        // 查询数据库中日成本
//        PolicyKpiModel policyKpi = policyKpiMapper.selectByPrimaryKey(policyId, day);
//        if (policyKpi != null) {
//            Long dailyBudget = policyKpi.getDailyBudget();
//            // TODO : 取数据模块中的日成本
//            Long dailyCost = null;
//            if (dailyBudget.longValue() <= dailyCost.longValue()) {
//                return true;
//            } else {
//                return false;
//            }
//        }
        return false;
    }

    /**
     * 判断Kpi是否已到上限
     *
     * @param policyId 策略id
     */
    public boolean isOverDailyKpi(Integer policyId) {
//        // 当前天
//        Date day = DateUtils.getCurrenDay();
//        // 查询日点击和日展现等信息
//        PolicyKpiModel policyKpi = policyKpiMapper.selectByPrimaryKey(policyId, day);
//        if (policyKpi != null) {
//            Long dailyImpression = policyKpi.getDailyImpression();
//            Long dailyClick = policyKpi.getDailyClick();
//            // TODO : 取数据模块中的日点击、日展现
//            Long impressionData = null;
//            Long clickData = null;
//            if (dailyImpression.longValue() <= impressionData.longValue() && dailyClick.longValue() <= clickData.longValue()) {
//                return true;
//            } else {
//                return false;
//            }
//        }
        return false;
    }

    /**
     * 策略下是否有创意
     */
    public boolean isHaveCreative(Integer policyId) {
        // 根据策略id查询策略和创意的关联信息
        List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(policyId);
        // 如果策略下有创意返回ture
        return policyCreatives.size() > 0;
    }

    /**
     * 判断是否在投放周期
     */
    public boolean isPolicyPeriod(Integer policyId) {
        // 查询策略信息
        PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
        // 策略的投放周期
        Date startDate = policy.getStartDate();
        Date endDate = policy.getEndDate();
        // 策略在投放周期，返回ture
        return isPeriod(startDate, endDate);
    }

    /**
     * 判断是否在投放周期
     *
     * @param startDate 开始投放时间
     * @param endDate 结束投放时间
     */
    public boolean isPeriod(Date startDate, Date endDate) {
        // 当前时间
        Date current = new Date();
        // 判断投放周期，在投放周期返回true
        return (startDate.before(current) && endDate.after(current));
    }

    /**
     * 判断策略的投放开始时间是在当前时间之前/之后
     */
    public String checkPolicyStartDate(Integer policyId) {
        // 查询策略信息
        PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
        // 策略的投放周期的开始时间
        Date startDate = policy.getStartDate();
        // 投放时间
        String strDateType = checkStartDate(startDate);
        return strDateType;
    }

    /**
     * 判断投放开始时间是在当前时间之前/之后
     */
    public String checkStartDate(Date startDate) {
        // 当前时间
        Date current = new Date();

        String strDateType = null;

        // 如果投放周期的开始时间在当前时间之前，返回“01”
        if (startDate.before(current)) {
            strDateType = START_DATE_BEFORE;
            return strDateType;
        }
        // 如果投放周期的开始时间在当前时间之后，返回“02”
        if (startDate.after(current)) {
            strDateType = START_DATE_AFTER;
            return strDateType;
        }
        return strDateType;
    }

    /**
     * 判断策略投放开始时间是今天
     */
    public boolean isPolicyTodayStart(Integer policyId) {
        // 查询策略信息
        PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
        // 策略投放的开始时间
        Date startDate = policy.getStartDate();
        // 判断策略投放开始时间是今天,返回true
        return isTodayStart(startDate);
    }

    /**
     * 判断投放开始时间是今天
     */
    public boolean isTodayStart(Date startDate) {
        // 获取当天
        Date today = DateUtils.getCurrenDay();
        // 判断投放开始时间是今天,返回true
        return startDate.equals(today);
    }

    /**
     * 判断策略的开关是否开启
     */
    public boolean isOpenPolicy(Integer policyId) {
        // 查询策略信息
        PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
        // 开关状态
        String enable = policy.getEnable();
        // 策略的开关打开返回true
        return isOpenSwitch(enable);
    }

    /**
     * 判断开关是否开启
     */
    public boolean isOpenSwitch(String enable) {
        // 开关打开返回true
        return StatusConstant.ON_STATUS.equals(enable);
    }

    /**
     * 判断策略投放周期是否已结束
     */
    public boolean isPolicyEndDate(Integer policyId) {
        // 查询策略信息
        PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
        // 结束时间
        Date endDate = policy.getEndDate();
        // 如果已结束返回true
        return isEndDate(endDate);
    }

    /**
     * 判断投放是否已结束
     */
    public boolean isEndDate(Date endDate) {
        // 当前时间
        Date current = new Date();
        // 如果已结束返回true
        return endDate.before(current);
    }

    /**
     * 查询指定策略的状态。
     *
     * @param policyId 投放策略ID
     * @return 状态机枚举
     */
    public PolicyState getState(Integer policyId) {
        // 判断投放策略是否已结束，如果已结束返回true
        boolean policyEnded = isPolicyEndDate(policyId);
        if (policyEnded) {
            return PolicyState.FINISH;
        }

        // 判断投放策略此时是否到达投放周期，如果在投放周期返回true
        boolean policyInPeriod = isPolicyPeriod(policyId);
        if (!policyInPeriod) {
            return PolicyState.OUT_OF_CYCLE;
        }

        // 判断策略的投放开关是否打开，如果开关是打开的返回true
        boolean policyIsOpened = isOpenPolicy(policyId);
        if (!policyIsOpened) {
            return PolicyState.MANUAL_SUSPEND;
        }

        // 判断当前策略是否在投放定向时间内，如果在投放时段返回true
        boolean policyInTargetTime = isOnTargetTime(policyId);
        if (!policyInTargetTime) {
            return PolicyState.OUT_OF_PHASE;
        }

        // 根据策略ID获得策略所属活动的活动ID
        PolicyVO policy = findById(policyId);
        Integer campaignId = null;
        if (policy != null) {
            campaignId = policy.getCampaignId();
        }

        if (campaignId != null) {
            // 判断策略所属的活动是否已暂停，如果活动已暂停返回true
            CampaignState campaignState = campaignService.getState(campaignId);
            boolean isCampaignSuspended = campaignService.isSuspended(campaignState);
            if (isCampaignSuspended) {
                return PolicyState.CAMPAIGN_HAS_SUSPEND;
            }

            // 判断当前策略是否已完成全部KPI，如果KPI达到返回true
            boolean policyKPIReached = isOverDailyKpi(policyId);
            if (policyKPIReached) {
                return PolicyState.OUT_OF_KPI;
            }

            // 判断当前策略是否已消耗光日成本，如果日成本已用完返回true
            boolean policyOutOfCost = false;
            if (policyOutOfCost) {
                return PolicyState.OUT_OF_COST;
            }

            return PolicyState.LAUNCHING;
        }

        return null;
    }

    /**
     * 判断策略是否是已暂停状态（未到投放周期、手动暂停、不在投放时段、所属推广活动已暂停、已到日KPI、已到日成本）。
     *
     * @param state 策略的状态枚举
     * @return 如果状态概念上属于已暂停，即括中描述的几种状态，则返回true，否则返回false
     */
    public boolean isSuspended(PolicyState state) {
        boolean result = false;

        switch (state) {
            case OUT_OF_CYCLE:
            case MANUAL_SUSPEND:
            case OUT_OF_PHASE:
            case CAMPAIGN_HAS_SUSPEND:
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
     * 判断策略是否是投放中状态。
     *
     * @param state 策略的状态枚举
     * @return 如果状态是LAUNCHING，则返回true，否则返回false
     */
    public boolean isLaunching(PolicyState state) {
        if (state == PolicyState.LAUNCHING) {
            return true;
        }
        return false;
    }

    /**
     * 判断策略是否是投放结束状态。
     *
     * @param state 策略的状态枚举
     * @return 如果状态是FINISH，则返回true，否则返回false
     */
    public boolean isFinished(PolicyState state) {
        if (state == PolicyState.FINISH) {
            return true;
        }
        return false;
    }

    /**
     * 投放策略创意开关
     */
    public void enableCreative(String enable, Integer id) {
        PolicyCreativeModel policyCreativeModel = policyCreativeMapper.selectByPrimaryKey(id);
        if (policyCreativeModel == null) {
            throw new DuplicateEntityException(PhrasesConstant.POLICY_CREATIVE_NOT_FOUND);
        }
        switch (enable) {
            case StatusConstant.ON_STATUS:
                policyCreativeMapper.updateStatusById(StatusConstant.ON_STATUS, id);
                boolean passAudit = creativeService.isPassAudit(policyCreativeModel.getCreativeId());
                boolean openPackageCreative = creativeService.isOpenPackageCreative(policyCreativeModel.getCreativeId());
                boolean b = isPolicyPeriod(policyCreativeModel.getPolicyId());
                if (passAudit&& openPackageCreative && b) {
                    try {
                        redisService.writeOneCreativeId(policyCreativeModel.getPolicyId(), id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case StatusConstant.OFF_STATUS:
                try {
                    policyCreativeMapper.updateStatusById(StatusConstant.OFF_STATUS, id);
                    redisService.removeOneCreativeId(policyCreativeModel.getPolicyId(), id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 判断是否编辑活动投放周期
     */
    public boolean isUpdatePolicyDate(PolicyVO currentPolicyVO, PolicyVO policyVO) {
        return currentPolicyVO.getStartDate().longValue() != policyVO.getStartDate().longValue()
            || currentPolicyVO.getEndDate().longValue() != policyVO.getEndDate().longValue();
    }

    /**
     * 判断是否编辑活动策略信息
     */
    public boolean isUpdatePolicyControl(PolicyVO currentPolicyVO, PolicyVO policyVO) {
        boolean result = false;

        String currentIsUniform = currentPolicyVO.getIsUniform();
        String isUniform = policyVO.getIsUniform();
        //比较匀速投放
        if (!ObjectUtils.equals(currentIsUniform, isUniform)) {
            result = true;
        }
        Long currentTotalImpression = currentPolicyVO.getTotalImpression();
        Long currentTotalClick = currentPolicyVO.getTotalClick();
        Long currentTotalBudget = currentPolicyVO.getTotalBudget();

        Long totalImpression = policyVO.getTotalImpression();
        Long totalClick = policyVO.getTotalClick();
        Long totalBudget = policyVO.getTotalBudget();
        //比较总展现量
        if (!ObjectUtils.equals(currentTotalImpression, totalImpression)) {
            result = true;
        }
        //比较总点击量
        if (!ObjectUtils.equals(currentTotalClick, totalClick)) {
            result = true;
        }
        //比较总预算
        if (!ObjectUtils.equals(currentTotalBudget, totalBudget)) {
            result = true;
        }

        PolicyKpiVO[] currentKpis = currentPolicyVO.getKpi();
        PolicyKpiVO[] kpis = policyVO.getKpi();
        for (int i = 0; i < currentKpis.length; i++) {
            Long currentDailyImpression = currentKpis[i].getDailyImpression();
            Long currentDailyClick = currentKpis[i].getDailyClick();
            Long currentDailyBudget = currentKpis[i].getDailyBudget();
            Long currentPeriod = currentKpis[i].getPeriod();
            Date currentDay = currentKpis[i].getDay();
            
            for(int j=0 ;j<kpis.length ;j++){
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
	                if (!ObjectUtils.equals(currentDailyImpression, dailyImpression)) {
	                    result = true;
	                }
	                //比较日点击
	                if (!ObjectUtils.equals(currentDailyClick, dailyClick)) {
	                    result = true;
	                }
	                //比较日预算
	                if (!ObjectUtils.equals(currentDailyBudget, dailyBudget)) {
	                    result = true;
	                }
                }
            }
        }
        //比较C端频次控制类型frequencytype
        String currentObjectType = currentPolicyVO.getObjectType();
        String objectType = policyVO.getObjectType();
        if (!ObjectUtils.equals(currentObjectType, objectType)) {
            result = true;
        }
        //比较C端频次控制行为frequencyaction
        String currentFrequencyType = currentPolicyVO.getFrequencyType();
        String frequencyType = policyVO.getFrequencyType();
        if (!ObjectUtils.equals(currentFrequencyType, frequencyType)) {
            result = true;
        }
        //比较C端频次控制周期frequencyperiod
        String currentCycleType = currentPolicyVO.getCycleType();
        String cycleType = policyVO.getCycleType();
        if (!ObjectUtils.equals(currentCycleType, cycleType)) {
            result = true;
        }
        //比较C端频次控制数量frequencycount
        Integer currentFrequencyAmount = currentPolicyVO.getFrequencyAmount();
        Integer frequencyAmount = currentPolicyVO.getFrequencyAmount();
        if (!ObjectUtils.equals(currentFrequencyAmount, frequencyAmount)) {
            result = true;
        }

        return result;
    }

    boolean isUpdateTargetTime(PolicyVO currentPolicyVO, PolicyVO policyVO) {
        boolean result = false;
        PolicyKpiVO[] currentKpis = currentPolicyVO.getKpi();
        PolicyKpiVO[] kpis = policyVO.getKpi();
        for (int i = 0; i < currentKpis.length; i++) {
        	for(int j =0;j <kpis.length; j++){
        		if(currentKpis[i].getDay().equals(kpis[j].getDay())){
        			 if (!ObjectUtils.equals(currentKpis[i].getPeriod(), kpis[j].getPeriod())) {
        	                result = true;
        	            }
        		}
        	}
        }
        return result;
    }

    /**
     * 策略下导入创意
     * @param policyId 	策略ID
     * @param map 包含 创意ID数组
     * @throws Exception
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void policyAddCreative(Integer policyId, Map<String, Object> map) throws Exception {
        List<Integer> creativeIds = (List<Integer>) map.get("creativeIds");
        if (creativeIds != null && creativeIds.size() > 0) {
            for (Integer creativeId : creativeIds) {
                PolicyModel policyModel = policyMapper.selectByPrimaryKey(policyId);
                if (policyModel != null) {
                    // 查询创意是否已存在
                    List<PolicyCreativeModel> PolicyCreativeModels = policyCreativeMapper.selectByPolicyIdAndCreativeId(policyId, creativeId);
                    if (PolicyCreativeModels != null && PolicyCreativeModels.size() > 0) {
                        continue;
                    }
                    // 加入表数据
                    PolicyCreativeModel policyCreativeModel = new PolicyCreativeModel();
                    policyCreativeModel.setCreativeId(creativeId);
                    policyCreativeModel.setPolicyId(policyId);
                    policyCreativeModel.setEnable(StatusConstant.OFF_STATUS); //开关默认关闭
                    // 创建时的价格是策略层设置的价格
                    policyCreativeModel.setBid(policyModel.getRealBid());
                    policyCreativeMapper.insert(policyCreativeModel); // 先插入，再更新状态

                    // 状态时需要判断，根据状态基判断
                    CreativeState state = creativeService.getState(policyCreativeModel.getId());
                    policyCreativeModel.setStatus(state.getCode());
                    policyCreativeMapper.updateByIdSelective(policyCreativeModel);
                    // 写入redis
                    if (isPolicyPeriod(policyId)) {
                        redisService.writeCreativeInfo(policyCreativeModel.getId());
                        redisService.writeCreateBid(policyCreativeModel.getId());
                    }
                } else {
                    throw new ResourceNotFoundException(PhrasesConstant.POLICY_NOT_FOUND);
                }
            }
        } else {
            throw new IllegalStatusException(PhrasesConstant.LACK_NECESSARY_PARAM);
        }
    }

    /**
     * 策略下导入物料包下创意
     * @param policyId 	策略ID
     * @param map 包含 物料包ID数组
     */
    public void policyAddPackageCreative(Integer policyId, Map<String, Object> map) throws Exception {
        List<Integer> packageIds = (List<Integer>) map.get("packageIds");
        if (packageIds != null && packageIds.size() > 0) {
            Map<String, Object> params;
            Map<String, Object> para;
            List<Integer> creativeIds;
            for (Integer packageId : packageIds) {
                // 查询物料包下“审核通过的创意”
                params = new HashMap<>();
                params.put("id", packageId);
                params.put("auditStatus", StatusConstant.CREATIVE_STATUS_APPROVED);
                List<CreativeModel> creativeModels = creativeMapper.selectCreatives(params);
                if (creativeModels != null && creativeModels.size() > 0) {
                    para = new HashMap<>();
                    creativeIds = new ArrayList<>();
                    for (CreativeModel creativeModel : creativeModels) {
                        creativeIds.add(creativeModel.getId());
                    }
                    para.put("creativeIds", creativeIds);
                    // 策略下导入创意
                    policyAddCreative(policyId, para);
                }
            }
        } else {
            throw new IllegalStatusException(PhrasesConstant.LACK_NECESSARY_PARAM);
        }
    }

    public void enablePolicy(PolicyModel policyModel) throws Exception {

        if (StatusConstant.ON_STATUS.equals(policyModel.getEnable())) {
            policyMapper.updateByIdSelective(policyModel);
            //策略的投放开始时间在当前时间之前 && 策略投放的结束时间在当前时间之后 && 策略在定向时间 && 活动打开 && 未到日KPI && 未到日成本
            if (isPolicyPeriod(policyModel.getId()) && isOnTargetTime(policyModel.getId()) && campaignService
                .isOpenCampaign(policyModel.getCampaignId()) && !isOverDailyKpi(policyModel.getId()) && !isOverDailyBudget(
                policyModel.getId())) {
                //将策略id写入策略ids中:所有的投放策略ID即dsp_policyids
                redisService.writePolicyId(policyModel.getId());
            }
        }
        if (StatusConstant.OFF_STATUS.equals(policyModel.getEnable())) {
            policyMapper.updateByIdSelective(policyModel);
            //将策略id从 策略ids中删除:所有的投放策略ID即dsp_policyids
            redisService.removePolicyId(policyModel.getId());
        }
    }

	public PolicyModel autoCreatePolicy(Integer campaignId, Integer bid) throws Exception {
		CampaignVO campaignVO = campaignService.findById(campaignId);
		if (campaignVO == null) {
            throw new ResourceNotFoundException(PhrasesConstant.CAMPAIGN_NOT_FOUND);
        }
		PolicyVO policyVO = campaignService.convertVO(campaignVO, bid.toString());
		if(DateUtils.getCurrenDay().getTime()>campaignVO.getStartDate().longValue()
				&& DateUtils.getCurrenDay().getTime()<campaignVO.getEndDate().longValue()){
			policyVO.setStartDate(DateUtils.getCurrenDay().getTime());
		}
		
		return savePolicy(policyVO);
	}

	public void updatePolicyBid(PolicyVO policyVO) throws Exception {
		Integer policyId = policyVO.getId();
		updatePolicy(policyId, policyVO, policyVO);
		 if (isHaveCreative(policyId)) {
             //根据策略id查询策略和创意的关联信息
             List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(policyId);
             if (policyCreatives != null) {
                 for (PolicyCreativeModel policyCreativeModel : policyCreatives) {
                	 Integer policyCreativeId = policyCreativeModel.getId();
                	 //修改策略下创意价格
                	 creativeService.updatePolicyCreativeBid(policyCreativeId, policyVO.getRealBid());
                 }
             }
		 }
	}
}
