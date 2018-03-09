package com.pxene.odin.cloud.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.pxene.odin.cloud.common.util.DateUtils;
import com.pxene.odin.cloud.domain.model.CampaignModel;
import com.pxene.odin.cloud.domain.model.PolicyCreativeModel;
import com.pxene.odin.cloud.domain.model.PolicyModel;
import com.pxene.odin.cloud.domain.vo.ContractVO;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyCreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyMapper;
import com.pxene.odin.cloud.web.api.ChannelAdxClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScheduleService
{
    //@Scheduled(fixedDelay = 30000)
//    public void testScheduleJobByFixDelay()
//    {
//        System.out.println("@@@ 计划任务开始执行 @@@");
//    }

	@Autowired
	private CampaignMapper campaignMapper;
	@Autowired
	private PolicyMapper policyMapper;
	@Autowired
	private RedisService redisService;
	@Autowired
	private PolicyService policyService;
	@Autowired 
	private CampaignService campaignService;
	@Autowired
	private PolicyCreativeMapper policyCreativeMapper;
	@Autowired
	private CreativeService creativeService;
	@Autowired
	private ChannelAdxClient channelAdxClient;
	
	@Scheduled(cron = "0 0 */1 * * ?")
    public void testScheduleJobByCrontab() throws Exception
    {		
        //System.out.println("### 计划任务开始执行 ###");
		// 当前小时
		String currentHour = DateUtils.getCurrentHour();
		// yyyy-MM-dd
		String currentDate = DateUtils.getCurrentDate();
		// 当前日期的整点时间
		Date todayStart = DateUtils.strToDate(currentDate, "yyyy-MM-dd");
		// 当前日期退后一秒钟的时间
		Date yesterdayEnd = DateUtils.changeDate(todayStart, Calendar.SECOND, -1);
		log.debug("<=DSP-Advertiser=> ScheduleService {昨天的时间是######}..." + yesterdayEnd);
		// 每天凌晨的任务
		if ("00".equals(currentHour)) {
			log.debug("<=DSP-Advertiser=> ScheduleService {每天零点的任务######}..." + currentDate + currentHour);
			// 今天开始投放的活动写入redis
			List<CampaignModel> startCampaigns = campaignMapper.findCampaignsByStartDate(todayStart);
			for (CampaignModel campaign : startCampaigns) {
				redisService.writeCampaignControl(campaign.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {写活动控制策略######}..." + campaign.getName());
				redisService.writeCampaignIds(campaign.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {写活动ids######}..." + campaign.getName());
			}
			// 今天开始投放的策略写入redis
			List<PolicyModel> startPolicys = policyMapper.findPolicysByStartDate(todayStart);			
			for (PolicyModel policy : startPolicys) {
				redisService.writePolicyInfo(policy.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {WriteCreativeInfo}..." + "写策略的基本信息#####");
				redisService.writePolicyControl(policy.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {WriteCreativeInfo}..." + "写策略控制策略#####");
				redisService.writePolicyTarget(policy.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {WriteCreativeInfo}..." + "写策略定向信息#####");
//				redisService.writeAdxContractBidInfo(policy.getId());
				if (policyService.isHaveCreative(policy.getId())) {
					// 如果策略下有创意
					List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(policy.getId());
					for (PolicyCreativeModel policyCreative : policyCreatives) {
						Integer mapId = policyCreative.getId();
						Integer creativeId = policyCreative.getCreativeId();
						// 将创意基本信息和出价信息写入redis
						redisService.writeCreativeInfo(mapId);
						redisService.writeCreateBid(mapId);
						if (creativeService.isOpenPolicyCreative(mapId) && 
								creativeService.isOpenPackageCreative(creativeId) && creativeService.isPassAudit(creativeId)) {
							// 将创意id写入到mapids中
							redisService.writeCreativeIds(policy.getId());
						}						
					}					
				}				
			}
			// 合同的开始时间为今天，将定价合同的信息写入redis中
			String todayContracts = channelAdxClient.selectContractByTodayStart(todayStart);
			if (todayContracts != null && !todayContracts.isEmpty()) {
				JsonParser contractParser = new JsonParser();
				List<ContractVO> contracts = new Gson().fromJson(contractParser.parse(todayContracts).getAsJsonArray().toString(),new TypeToken<List<ContractVO>>(){}.getType());
				if (contracts != null && !contracts.isEmpty()) {
					for (ContractVO contract : contracts) {
						redisService.writeAdxContractBidInfo(contract.getId());
					}
				}
			}
			
			// 今天结束投放的活动从redis中删除
			List<CampaignModel> endCampaigns = campaignMapper.findCampaignsByEndDate(yesterdayEnd);
			log.debug("<=DSP-Advertiser=> ScheduleService {今天结束投放的活动######}..." + endCampaigns.size());
			for (CampaignModel campaign : endCampaigns) {
				redisService.removeCampaignControl(campaign.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {RemoveCampaignControl}..." + "删除活动控制策略信息######");
				redisService.removeCampaignId(campaign.getId());
				log.debug("<=DSP-Advertiser=> ScheduleService {RemoveCampaignID}..." + "删除活动ids######");
			}
			// 今天结束投放的策略从redis中删除
			List<PolicyModel> endPolicys = policyMapper.findPolicysByEndDate(yesterdayEnd);
			log.debug("<=DSP-Advertiser=> ScheduleService {今天结束投放的策略######}..." + endPolicys.size());
			for (PolicyModel policy : endPolicys) {
				redisService.removePolicyInfo(policy.getId());
				redisService.removePolicyControl(policy.getId());
				redisService.removePolicyTarget(policy.getId());
				redisService.removePolicyId(policy.getId());
//				redisService.removeAdxContractBidInfo(policy.getId());
				if (policyService.isHaveCreative(policy.getId())) {
					// 如果策略下有创意
					redisService.removeCreativeInfo(policy.getId());
					redisService.removeCreativeBid(policy.getId());
					redisService.removeCreativeIds(policy.getId());
				}
				log.debug("<=DSP-Advertiser=> ScheduleService {RemovePolicyInfo}..." + "删除策略相关的所有信息######");
			}
			// 合同的截止时间为昨天从redis中删除其合同定价信息
			String yesterdayContracts = channelAdxClient.selectContractByYesterdayEnd(yesterdayEnd);
			if (yesterdayContracts != null && !yesterdayContracts.isEmpty()) {
				JsonParser contractParser = new JsonParser();
				List<ContractVO> contracts = new Gson().fromJson(contractParser.parse(yesterdayContracts).getAsJsonArray().toString(),new TypeToken<List<ContractVO>>(){}.getType());
				if (contracts != null && !contracts.isEmpty()) {
					for (ContractVO contract : contracts) {
						redisService.removeAdxContractBidInfo(contract.getId());
					}
				}
			}
		}
		
		// 每小时的任务
		// 所有在投放周期内的策略
		Date today = new Date();
		List<PolicyModel> currentPolicys = policyMapper.findPolicysByCurrentDate(today);
		log.debug("<=DSP-Advertiser=> ScheduleService {所有在投放周期内的策略######}..." + currentPolicys.size());
		// 每个小时判断时间定向，将满足条件的策略id加入、将不在该时间内的策略id移除
		if (currentPolicys != null && !currentPolicys.isEmpty()) {
			for (PolicyModel policy : currentPolicys) {
				Integer policyId = policy.getId();
				if (policyService.isOnTargetTime(policyId)) {
					log.debug("<=DSP-Advertiser=> ScheduleService {策略在定向时间内######}...");
					// 如果在策略定向时间
					if (policyService.isPolicyPeriod(policyId) && policyService.isOpenPolicy(policyId) &&
							!policyService.isOverDailyBudget(policyId) && !policyService.isOverDailyKpi(policyId) 
							&& campaignService.isOpenCampaign(policy.getCampaignId())) {
						// 如果在投放时间 && 活动开启 && 策略开启 && 未到日KPI && 未到日成本，将策略id写入redis中
						redisService.writePolicyId(policyId);
						log.debug("<=DSP-Advertiser=> ScheduleService {WritePolicyId}..." + "写入策略ids######");
					}
				} else {
					// 如果不在定向时间，则将其从redis中删除
					redisService.removePolicyId(policyId);
					log.debug("<=DSP-Advertiser=> ScheduleService {RemovePolicyId}..." + "删除策略ids######");
				}
			}
		}		
    }
	
	@Scheduled(cron="* 0/5 *  * * ? ")
	public void writeAdsettingWorking() throws Exception {
		redisService.writeAdsettingWorking();
	}
}
