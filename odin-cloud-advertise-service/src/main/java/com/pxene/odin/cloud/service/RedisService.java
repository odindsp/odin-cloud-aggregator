package com.pxene.odin.cloud.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pxene.odin.cloud.common.constant.CodeTableConstant;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.RedisKeyConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.common.constant.TargetTypeConstant;
import com.pxene.odin.cloud.common.util.GeoHash;
import com.pxene.odin.cloud.common.util.GlobalUtil;
import com.pxene.odin.cloud.common.util.RedisHelper;
import com.pxene.odin.cloud.domain.model.AdvertiserAuditModel;
import com.pxene.odin.cloud.domain.model.AdvertiserModel;
import com.pxene.odin.cloud.domain.model.CampaignKpiModel;
import com.pxene.odin.cloud.domain.model.CampaignModel;
import com.pxene.odin.cloud.domain.model.CreativeMaterialModel;
import com.pxene.odin.cloud.domain.model.CreativeModel;
import com.pxene.odin.cloud.domain.model.ImageModel;
import com.pxene.odin.cloud.domain.model.PackageModel;
import com.pxene.odin.cloud.domain.model.PolicyCreativeModel;
import com.pxene.odin.cloud.domain.model.PolicyKpiModel;
import com.pxene.odin.cloud.domain.model.PolicyModel;
import com.pxene.odin.cloud.domain.model.PolicyTargetingModel;
import com.pxene.odin.cloud.domain.model.ProjectModel;
import com.pxene.odin.cloud.domain.model.VideoModel;
import com.pxene.odin.cloud.domain.vo.ContractVO;
import com.pxene.odin.cloud.domain.vo.RegionVO;
import com.pxene.odin.cloud.domain.vo.SizeVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.repository.mapper.basic.AdvertiserAuditMapper;
import com.pxene.odin.cloud.repository.mapper.basic.AdvertiserMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignKpiMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CreativeMaterialMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ImageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PackageMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyCreativeMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyKpiMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyMapper;
import com.pxene.odin.cloud.repository.mapper.basic.PolicyTargetingMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ProjectMapper;
import com.pxene.odin.cloud.repository.mapper.basic.VideoMapper;
import com.pxene.odin.cloud.web.api.BasicRegionClient;
import com.pxene.odin.cloud.web.api.ChannelAdxClient;
import com.pxene.odin.cloud.web.api.ChannelSizeClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author zhengyi
 * 所有涉及到redis操作都写在这里面
 */
@Service
@Slf4j
public class RedisService extends BaseService{
	
	private static String IMAGE_URL;
	
	@Autowired
	public RedisService(Environment env) {
		IMAGE_URL = env.getProperty("dsp.fileserver.remote.url.prefix");
	}

	@Autowired
	private RedisHelper redisHelper;
	
	@PostConstruct
	public void selectRedis() {
		redisHelper.select("redis.primary.");
	}
	
	@Autowired
	CampaignMapper campaignMapper;
	
	@Autowired
	ProjectMapper projectMapper;
	
	@Autowired
	AdvertiserMapper advertiserMapper;
	
	@Autowired
	PolicyTargetingMapper policyTargetingMapper;
	
	@Autowired
	AdvertiserAuditMapper advertiserAuditMapper;
	
	@Autowired
	PolicyCreativeMapper policyCreativeMapper;
	
	@Autowired
	CreativeService creativeService;
	
	@Autowired
	PolicyMapper policyMapper;
	
	@Autowired
	PolicyTargetingMapper policyTargetMapper;
	
	@Autowired
	BasicRegionClient basicRegionClient;
	
	@Autowired
	PolicyKpiMapper policyKpiMapper;
	
	@Autowired
	CreativeMapper creativeMapper;
	
	@Autowired
	CampaignKpiMapper campaignKpiMapper;
	
	@Autowired
	CreativeMaterialMapper creativeMaterialMapper;
	
	@Autowired
	ImageMapper imageMapper;
	
	@Autowired
	ChannelSizeClient channelSizeClient;
	
	@Autowired
	PackageMapper packageMapper;
	
	@Autowired
	VideoMapper videoMapper;	
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	ChannelAdxClient channelAdxClient;
	
	/**
	 * 到开始日期的任务调用该方法，将策略基本信息写入redis
	 * @param policy
	 * @throws Exception
	 */
//	public void writePolicyAllInfo(Integer policyId) throws Exception {
//		// 投放策略的基本信息
//		writePolicyInfo(policyId);
//		// 投放策略的定向信息
//		writePolicyTarget(policyId);
//		// 投放策略引用的人群包
//		writeWhiteBlack(policyId);
//		// 策略的投放控制策
//		writePolicyControl(policyId);
//	}
	
	/**
	 * 到结束日期的任务调用该方法，将redis内所有相关内容全部清除
	 * @param policy
	 * @throws Exception
	 */
//	public void removePolicyAllInfo(Integer policyId) throws Exception {
//		// 移除策略id
//		removePolicyId(policyId);
//		// 移除mapids
//		removeCreativeIds(policyId);
//		// 移除策略的基本信息
//		removePolicyInfo(policyId);
//		// 移除策略定向信息
//		removePolicyTarget(policyId);
//		// 移除创意基本信息
//		removeCreativeInfo(policyId);
//		// 移除创意出价信息
//		removeCreativeBid(policyId);
//		// 移除策略控制策略信息
//		removePolicyControl(policyId);
//	}
	
	/**
	 * 将策略id写入策略ids中:所有的投放策略ID即dsp_policyids
	 * @param policyId
	 * @return
	 * @throws Exception
	 */
	public void writePolicyId(Integer policyId) throws Exception {
		if (policyId != null) {
			String strPolicyKey = RedisKeyConstant.POLICY_IDS;
			// 从redis中获取策略ids这个key 
			Set<String> strPolicyIds = redisHelper.sget(RedisKeyConstant.POLICY_IDS);
			List<String> policyIds = new ArrayList<String>();
			String strPolicyId = String.valueOf(policyId);
			if (strPolicyIds == null || strPolicyIds.isEmpty()) {
				policyIds.add(strPolicyId);
			} else {
				// 判断将要写入的策略id在不在dsp_policyids中
				if (!strPolicyIds.contains(strPolicyId)) {
					policyIds.add(strPolicyId);
				}
			}
			// 将策略id写入到dsp_policyids中
			redisHelper.addKey(strPolicyKey, policyIds);
		}		
	}
	
	/**
	 * 投放策略下所有的创意ID写入redis : dsp_policy_mapids_(policyid)
	 * @param policyId
	 * @throws Exception
	 */
	public void writeCreativeIds(Integer policyId) throws Exception {
		// 根据策略id查询创意和策略关联信息
		List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(policyId);
		// 创意ids
		List<String> creativeIds = new ArrayList<String>();
		if (policyCreatives != null && !policyCreatives.isEmpty()) {
			for (PolicyCreativeModel policyCreative : policyCreatives) {
				Integer id = policyCreative.getId();
				if (id != null) {
					Integer creativeId = policyCreative.getCreativeId();
					if (creativeService.isOpenPackageCreative(creativeId) && 
							creativeService.isOpenPolicyCreative(id) && 
							creativeService.isPassAudit(creativeId)){
						// 如果物料包下的创意打开 && 策略下的创意打开 && 物料包下创意审核通过，将创意id写入到mapids中					
						String strId = String.valueOf(id);
						creativeIds.add(strId);
					}	
				}							
			}
		}
		// 将创意ids写入到redis中
		redisHelper.addKey(RedisKeyConstant.POLICY_CREATIVEIDS + policyId, creativeIds);
	}
	
	/**
	 * 将策略的基本信息写入redis : dsp_policy_info_(policyid)
	 * @param policy
	 * @throws Exception
	 */
	public void writePolicyInfo(Integer policyId) throws Exception {
		PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
		if (policy != null) {
			
			JsonObject policyJson = new JsonObject();			
			JsonArray catJsons = new JsonArray();			
			
			// 所属活动ID
			Integer campaignId = policy.getCampaignId();
			if (campaignId != null) {
				policyJson.addProperty("campaignid", campaignId);
			}
			// 根据活动id查询活动信息
			Integer advertiserId = null;
			CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
			if (campaign != null) {
				Integer projectId = campaign.getProjectId();
				// 根据项目id查询项目信息
				ProjectModel project = projectMapper.selectProjectById(projectId);
				if (project != null) {
					 advertiserId = project.getAdvertiserId();
					// 根据广告主id查询广告主信息
					AdvertiserModel advertiser = advertiserMapper.selectByPrimaryKey(advertiserId);						
					if (advertiser != null) {
						Integer industryId = advertiser.getIndustryId();
						// 广告主行业类型
						catJsons.add(industryId);
						policyJson.add("cat", catJsons);
						// 广告行业分类
						policyJson.addProperty("advcat", industryId);
						// 广告主域名
						String websiteUrl = advertiser.getWebsiteUrl();
						if (websiteUrl != null && !websiteUrl.isEmpty()) {
							String adomain = GlobalUtil.parseString(websiteUrl, "").replace("http://www.", "").replace("www.", "");
							policyJson.addProperty("adomain", adomain);
						}				
					}		
				}								
			}
			
			JsonArray adxJsons = new JsonArray();
			JsonArray extJsons = new JsonArray();
			JsonArray auctiontypeJsons = new JsonArray();
			// 通过策略id查找渠道定向id
			String type = TargetTypeConstant.ADX_TAGET;
			List<PolicyTargetingModel> policyTargetings = policyTargetingMapper.selectByPolicyIdAndType(policyId, type);
			if (policyTargetings != null && !policyTargetings.isEmpty()) {
				for (PolicyTargetingModel policyTargeting : policyTargetings) {
					// 允许投放的Adx编号
					String strAdxId = policyTargeting.getValue();
					if (strAdxId != null && !strAdxId.isEmpty()) {
						Integer adxId = Integer.parseInt(strAdxId);
						adxJsons.add(adxId);
						
						// 竞价类型列表
						JsonObject auctiontypeJson = new JsonObject();
						auctiontypeJson.addProperty("adx", adxId);
						auctiontypeJson.addProperty("at", 0);
						auctiontypeJsons.add(auctiontypeJson);
						
						// 广告主在adx注册信息 
						if (advertiserId != null) {
							AdvertiserAuditModel advertiserAudit = advertiserAuditMapper.selectByPrimaryKey(advertiserId, adxId);
							if (advertiserAudit != null) {
								JsonObject extJson = new JsonObject();
								extJson.addProperty("adx", adxId);
								Integer auditValue = advertiserAudit.getAuditValue();
								if (auditValue != null) {
									extJson.addProperty("advid", auditValue.toString());
								}
								extJsons.add(extJson);
							}
						}						
					}					
				}
			}
			
			policyJson.add("adx", adxJsons);
			policyJson.add("auctiontype", auctiontypeJsons);
			policyJson.add("exts", extJsons);
			// 不重定向创意的curl
			policyJson.addProperty("redirect", 0);
			// 需要效果监测
			policyJson.addProperty("effectmonitor", 0);
			
			// 将策略基本信息写入到redis中
			redisHelper.set(RedisKeyConstant.POLICY_INFO + policyId , policyJson.toString());
		}		
	}
	
	/**
	 * 投放策略的定向信息写入redis : dsp_policyid_target_(policyid)
	 * @param policyId
	 * @throws Exception
	 */
	public void writePolicyTarget(Integer policyId) throws Exception {
		// 定向信息
		JsonObject targetJson = new JsonObject();
		// 设备信息定向
		JsonObject deviceJson = new JsonObject();
		// 策略的定向信息
		List<PolicyTargetingModel> policyTargets = policyTargetMapper.selectByPolicyId(policyId);
		if (policyTargets != null && !policyTargets.isEmpty()) {
			int flag = 0;
			// 所属的活动id
			PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
			if (policy != null) {
				if (policy.getCampaignId() != null) {
					String strCampaignId = policy.getCampaignId().toString();
					targetJson.addProperty("campaignid", strCampaignId);
				}
			}
			if (policyId != null) {
				// 策略ID
				String strPolicyId = policyId.toString();
				targetJson.addProperty("policyid", strPolicyId);
			}			
			// 1.地点定向编码列表
			List<PolicyTargetingModel> regionTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.REGION_TAGET);
			if (regionTargets != null && !regionTargets.isEmpty()) {
				// 地域定向：redis中写的是市id，如果选项的是省，找到省下的市id写入
				JsonArray regions = new JsonArray();
				// 查询所有市的id 
				String strRegions = basicRegionClient.selectRegions();				
				if (strRegions == null || strRegions.isEmpty()) {
	                throw new DuplicateEntityException(PhrasesConstant.GET_REGION_INFO_FAILED);
	            }
				JsonParser regionsParser = new JsonParser();
				List<RegionVO> listRegions = new Gson().fromJson(regionsParser.parse(strRegions).getAsJsonArray().toString(), new TypeToken<List<RegionVO>>(){}.getType());
				if (listRegions != null && !listRegions.isEmpty()) {
					List<String> cityIds = new ArrayList<String>();
					for (RegionVO listRegion : listRegions) {
						String regionId = listRegion.getId();
						if (!"000000".equals(regionId)) {
							if (!regionId.endsWith("0000")) {
								// 不以“0000”结尾的市
								cityIds.add(regionId);
							}
						}
					}
					// 判断是省还是市
					for (PolicyTargetingModel regionTarget : regionTargets) {
						String targetRegionId = regionTarget.getValue();
						if (!"000000".equals(targetRegionId)) {
							// 如果不是未知
							String provinceStr = targetRegionId.substring(0, 2);
							if (targetRegionId.endsWith("0000")) {
								if (provinceStr.equals("11") || provinceStr.equals("12")
										|| provinceStr.equals("31") || provinceStr.equals("50")) {
									regions.add(targetRegionId);
								} else {
									// 以“0000”结尾的并且不是直辖市的是省，将省下的市id添加到region中
									for (String cityId : cityIds) {
										String pId = targetRegionId.substring(0, 4) + "00";
										String cId = cityId.substring(0, 2) + "0000";
										if (pId.equals(cId)) {
											// 如果市属于定向的省，则将市添加
											regions.add(cityId);
										}
									}
								}
							} else {
								// 除了省份就是城市
								regions.add(targetRegionId);
							}
						}
					}
					flag = flag | RedisKeyConstant.TARGET_CODES.get("region")[1];	
					deviceJson.add("regioncode", regions);
				}												
			}
			// 2.网络定向编码列表
			List<PolicyTargetingModel> networkTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.NETWORK_TAGET);
			if (networkTargets != null && !networkTargets.isEmpty()) {
				JsonArray networkArray = new JsonArray();
				for (PolicyTargetingModel networkTarget : networkTargets) {
					networkArray.add(networkTarget.getValue());
				}
				flag = flag | RedisKeyConstant.TARGET_CODES.get("network")[1];
				deviceJson.add("connectiontype", networkArray);
			}
			// 3.系统定向编码列表
			List<PolicyTargetingModel> osTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.OS_TAGET);
			if (osTargets != null && !osTargets.isEmpty()) {
				JsonArray osArray = new JsonArray();
				for (PolicyTargetingModel osTarget : osTargets) {
					osArray.add(osTarget.getValue());
				}
				flag = flag | RedisKeyConstant.TARGET_CODES.get("os")[1];
				deviceJson.add("os", osArray);
			}
			// 4.运营商定向编码列表
			List<PolicyTargetingModel> operatorTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.CARRIER_TAGET);
			if (operatorTargets != null && !operatorTargets.isEmpty()) {
				JsonArray operatorArray = new JsonArray();
				for (PolicyTargetingModel operatorTarget : operatorTargets) {
					operatorArray.add(operatorTarget.getValue());
				}
				flag = flag | RedisKeyConstant.TARGET_CODES.get("operator")[1];
				deviceJson.add("carrier", operatorArray);
			}
			// 5.设备定向编码列表
			List<PolicyTargetingModel> deviceTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.DEVICE_TAGET);
			if (deviceTargets != null && !deviceTargets.isEmpty()) {
				JsonArray deviceArray = new JsonArray();
				for (PolicyTargetingModel deviceTarget : deviceTargets) {
					deviceArray.add(deviceTarget.getValue());
				}
				flag = flag | RedisKeyConstant.TARGET_CODES.get("device")[1];
				deviceJson.add("devicetype", deviceArray);
			}
			// 6.设备制作商编码列表
			List<PolicyTargetingModel> brandTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.BRAND_TAGET);
			if (brandTargets != null && !brandTargets.isEmpty()) {
				JsonArray brandArray = new JsonArray();
				for (PolicyTargetingModel brandTarget : brandTargets) {
					brandArray.add(brandTarget.getValue());
				}
				flag = flag | RedisKeyConstant.TARGET_CODES.get("brand")[1];
				deviceJson.add("make", brandArray);
			}
			deviceJson.addProperty("flag", flag);
			targetJson.add("device", deviceJson);
			
			// 应用程序信息定向，即app定向
			List<PolicyTargetingModel> appTargets = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.APP_TAGET);
			if (appTargets != null && !appTargets.isEmpty()) {
				JsonArray appJsons = new JsonArray();
				Map<String, JsonObject> adxMap = new HashMap<String, JsonObject>();
				JsonObject appObjects = new JsonObject();
				for (PolicyTargetingModel appTarget : appTargets) {
					// 获取adxId与appId，策略定向表中app定向的value由adxId与appId组成
					String appTargetValue = appTarget.getValue();
					String isInclude = appTarget.getIsInclude();
					if (appTargetValue.contains("|")) {
						String[] result = appTargetValue.split("\\|");
						String adxId = result[0];
						String appId = result[1];
						if (adxMap.containsKey(adxId)) {
							JsonObject adxJson = adxMap.get(adxId);
							if (isInclude.equals(StatusConstant.INCLUDE_TYPE_VALUE)) {
								JsonArray appIdJsons = adxJson.get("wlist").getAsJsonArray();
								appIdJsons.add(appId);
								adxJson.add("wlist", appIdJsons);
								adxMap.put(adxId, adxJson);
							} else if (isInclude.equals(StatusConstant.EXCLUDE_TYPE_VALUE)) {
								JsonArray appIdJsons = adxJson.get("blist").getAsJsonArray();
								appIdJsons.add(appId);
								adxJson.add("blist", appIdJsons);
								adxMap.put(adxId, adxJson);
							}
							
						} else {
							if (isInclude.equals(StatusConstant.INCLUDE_TYPE_VALUE)) {
								JsonObject adxJson = new JsonObject();
								adxJson.addProperty("adx", Integer.parseInt(adxId));
								adxJson.addProperty("flag", 1);
								JsonArray appIdJsons = new JsonArray();
								appIdJsons.add(appId);
								adxJson.add("wlist", appIdJsons);
								adxMap.put(adxId, adxJson);
							} else if (isInclude.equals(StatusConstant.EXCLUDE_TYPE_VALUE)){
								JsonObject adxJson = new JsonObject();
								adxJson.addProperty("adx", Integer.parseInt(adxId));
								adxJson.addProperty("flag", 2);
								JsonArray appIdJsons = new JsonArray();
								appIdJsons.add(appId);
								adxJson.add("blist", appIdJsons);
								adxMap.put(adxId, adxJson);
							}							
						}
					}					
				}
				for (Entry<String, JsonObject> entry : adxMap.entrySet()) {
					appJsons.add(entry.getValue());
				}
				appObjects.add("id", appJsons);
				targetJson.add("app", appObjects);
			}			
		}
		// 场景定向
		PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
		JsonArray locJsons = new JsonArray();
		JsonObject sceneObject = new JsonObject();
		if (policy != null) {
			String scenePath = policy.getScenePath();
			String sceneRadius = policy.getSceneRadius();			
			if (scenePath != null && !scenePath.isEmpty()) {
				sceneObject.addProperty("flag", 1);
				if (sceneRadius != null && !sceneRadius.isEmpty()) {
					sceneObject.addProperty("length", Integer.valueOf(sceneRadius));
				}
				JsonObject locObject = new JsonObject();
				// 经纬度
				List<Map<String,String>> coordinatesMap = fileUploadService.readGeoExcel(scenePath);
				if (coordinatesMap != null && !coordinatesMap.isEmpty()) {
					for (Map<String, String> coordinateMap : coordinatesMap) {
						String latitude = coordinateMap.get("Lat");
						if (latitude != null && !latitude.isEmpty()) {
							// 纬度
							Double latDouble = Double.parseDouble(latitude);
							locObject.addProperty("lat", latDouble);
						}
						String longitude = coordinateMap.get("Lng");
						if (longitude != null && !longitude.isEmpty()) {
							// 经度
							Double lonDouble = Double.parseDouble(longitude);
							locObject.addProperty("lon", lonDouble);
						}
						if (latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()) {
							// id
							Double latDouble = Double.parseDouble(latitude);
							Double lonDouble = Double.parseDouble(longitude);
							String id = GeoHash.encode(latDouble, lonDouble);
							if (id != null && !id.isEmpty()) {
								locObject.addProperty("id", id);
							}
						}
						locJsons.add(locObject);
					}
					sceneObject.add("loc", locJsons);
				}
				targetJson.add("scene", sceneObject);
			}			
		}
		
		// 将策略信息写入到redis中
		redisHelper.set(RedisKeyConstant.POLICY_TARGET + policyId, targetJson.toString());
	}

	/**
	 * 投放策略引用的人群包写入redis : dsp _policy_audienceid_(policyid)
	 * @param policyId
	 * @throws Exception
	 */
	public void writeWhiteBlack(Integer policyId) throws Exception {
		
	}
	
	/**
	 * 单个创意的详细信息写入redis : dsp_mapid_(mapid)
	 * @param mapId 策略-创意关联表中的id
	 * @throws Exception
	 */
	public void writeCreativeInfo(Integer mapId) throws Exception {
		// 通过策略-创意关联id查询信息
		PolicyCreativeModel policyCreative = policyCreativeMapper.selectByPrimaryKey(mapId);
		if (policyCreative != null) {
			// 创意id，即物料包下的创意id
			Integer creativeId = policyCreative.getCreativeId();
			// 创意信息
			CreativeModel creative = creativeMapper.selectByPrimaryKey(creativeId);
			if (creative != null) {
				// 创意类型：图片、视频、信息流
				String type = creative.getType();
				if (type != null && !type.isEmpty()) {
					if (CodeTableConstant.CREATIVE_TYPE_IMG.equals(type)) {
						// 图片创意
						writeImgCreativeInfo(policyCreative,creative);
					} else if (CodeTableConstant.CREATIVE_TYPE_INFO.equals(type)) {
						// 信息流创意
						writeInfoflowCreativeInfo(policyCreative,creative);
					} else if (CodeTableConstant.CREATIVE_TYPE_VIDEO.equals(type)) {
						// 视频创意
						writeVideoCreativeInfo(policyCreative,creative);
					}
				}				
			}
		}	
	}
	
	/**
	 * 图片创意信息写入redis
	 * @param policyCreative
	 * @param creative
	 * @throws Exception
	 */
	private void writeImgCreativeInfo(PolicyCreativeModel policyCreative, CreativeModel creative) throws Exception {
		// 策略-创意关联id
		Integer policyCreativeId = policyCreative.getId();
		// 创意id，即物料包下的创意id
		Integer packageCreativeId = creative.getId();
		
		// 创意基本信息,如下:
		JsonObject creativeObject = new JsonObject();
		// 单个策略下的创意ID
		creativeObject.addProperty("mapid", policyCreativeId);		
		// 创意所属的投放策略ID
		creativeObject.addProperty("policyid", policyCreative.getPolicyId());		
		// 奥丁平台中该创意的ID
		creativeObject.addProperty("creativeid", packageCreativeId);		
		// 广告创意类型
		creativeObject.addProperty("type", 2);
		// 根据创意id查询创意-素材信息
		List<CreativeMaterialModel> creativeMaterials = creativeMaterialMapper.selectByCreativeId(packageCreativeId);
		if (creativeMaterials != null && !creativeMaterials.isEmpty()) {
			for (CreativeMaterialModel creativeMaterial : creativeMaterials) {
				Integer materialId = creativeMaterial.getMaterialId();
				String materialType = creativeMaterial.getMaterialType();
				// 根据素材id查询素材信息
				if (materialType != null && !materialType.isEmpty() && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_IMAGE)) {
					ImageModel image = imageMapper.selectByPrimaryKey(materialId);					
					// 广告创意文件类型	
					creativeObject.addProperty("ftype", image.getFormatId());
					// 素材地址
			        creativeObject.addProperty("sourceurl", IMAGE_URL + image.getPath());
					// 根据尺寸id查询尺寸信息
					Integer sizeId = image.getSizeId();
					String size = channelSizeClient.selectSizeById(sizeId);
					if (size == null || "".equals(size)) {
						throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
					}

					JsonParser parser = new JsonParser();

					JsonObject jsonObject = parser.parse(size).getAsJsonObject();
					SizeVO sizeVO = new Gson().fromJson(jsonObject.toString(), SizeVO.class);

					// 广告宽度
					creativeObject.addProperty("w", sizeVO.getWidth());
					// 广告高度
					creativeObject.addProperty("h", sizeVO.getHeight());
				}
			}
		}
		// 点击目标类型
		creativeObject.addProperty("ctype", 1);
		// 根据物料包id查询物料包信息
		PackageModel packageModel = packageMapper.selectByPrimaryKey(creative.getPackageId());
		// 点击广告的着陆页
		if (packageModel.getLandpageUrl() != null && !packageModel.getLandpageUrl().isEmpty()) {
			creativeObject.addProperty("landingurl", GlobalUtil.parseString(packageModel.getLandpageUrl(), ""));
		}		
		
		// 第三方广告展示监测地址
		List<String> tempImonitorUrl = new ArrayList<String>(); 
		String impressionUrl1 = packageModel.getImpressionUrl1();
		if (impressionUrl1 != null && !impressionUrl1.isEmpty()) {
			tempImonitorUrl.add(GlobalUtil.parseString(impressionUrl1, ""));
		}
		String impressionUrl2 = packageModel.getImpressionUrl2();
		if (impressionUrl2 != null && !impressionUrl2.isEmpty()) {
			tempImonitorUrl.add(GlobalUtil.parseString(impressionUrl2, ""));
		}		 
		String[] strTempImonitorUrl = new String[tempImonitorUrl.size()]; //创建一个String型数组
		if (strTempImonitorUrl != null && strTempImonitorUrl.length > 0) {
			tempImonitorUrl.toArray(strTempImonitorUrl); //将list数组转换成String数组
		}		
		JsonArray imoUrlStr = new JsonArray();
		// 第三方广告点击监测地址
		List<String> tempCmonitorUrl = new ArrayList<String>();
		String clickUrl = packageModel.getClickUrl();
		if (clickUrl != null && !clickUrl.isEmpty()) {
			tempCmonitorUrl.add(GlobalUtil.parseString(packageModel.getClickUrl(), ""));
		}		
		JsonArray cmoUrlStr = new JsonArray();
		// 第三方广告监测代码片段
        String monitorcode = "";
        for (int i = 0; i < strTempImonitorUrl.length; i++) {
            imoUrlStr.add(strTempImonitorUrl[i]);
            monitorcode += RedisKeyConstant.MONITOR_TEMPLATES[0].replace("{index}", "" + i).replace("{imonitorurl}", strTempImonitorUrl[i]);
        }
        for (String cmonitorUrl : tempCmonitorUrl) {
            cmoUrlStr.add(cmonitorUrl);
        }
        creativeObject.add("imonitorurl", imoUrlStr);
        creativeObject.add("cmonitorurl", cmoUrlStr);
        creativeObject.addProperty("monitorcode", monitorcode);        
        // Campaign ID
        PolicyModel policy = policyMapper.selectByPrimaryKey(policyCreative.getPolicyId());
        CampaignModel campagin = campaignMapper.selectByPrimaryKey(policy.getCampaignId());
        if (campagin.getId() != null) {
        	creativeObject.addProperty("cid", GlobalUtil.parseString(campagin.getId(), ""));
        }        
		
		redisHelper.set(RedisKeyConstant.CREATIVE_INFO + policyCreativeId, creativeObject.toString());
	}
	
	/**
	 * 信息流创意信息写入redis
	 * @param policyCreative
	 * @param creative
	 * @throws Exception
	 */
	private void writeInfoflowCreativeInfo(PolicyCreativeModel policyCreative, CreativeModel creative) throws Exception {
		// 策略-创意关联id
		Integer policyCreativeId = policyCreative.getId();
		// 创意id，即物料包下的创意id
		Integer packageCreativeId = creative.getId();
						
		// 创意基本信息,如下:
		JsonObject creativeObject = new JsonObject();
		// 单个策略下的创意ID
		creativeObject.addProperty("mapid", policyCreativeId);	
		// 创意所属的投放策略ID
		creativeObject.addProperty("policyid", policyCreative.getPolicyId());		
		// 奥丁平台中该创意的ID
		creativeObject.addProperty("creativeid", packageCreativeId);		
		// 广告创意类型
		creativeObject.addProperty("type", 9);
		// 点击目标类型
		creativeObject.addProperty("ctype", 1);
		
		// 根据创意id查询创意-素材信息
		List<CreativeMaterialModel> creativeMaterials = creativeMaterialMapper.selectByCreativeId(packageCreativeId);
		if (creativeMaterials != null && !creativeMaterials.isEmpty()) {
			JsonArray imageJsons = new JsonArray(); 
			for (CreativeMaterialModel creativeMaterial : creativeMaterials) {
				Integer materialId = creativeMaterial.getMaterialId();
				String materialType = creativeMaterial.getMaterialType();
				// 根据素材id查询素材信息
				if (materialType != null && !materialType.isEmpty() && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_IMAGE)) {
					ImageModel image = imageMapper.selectByPrimaryKey(materialId);	
					JsonObject imageJson = new JsonObject();
					// 广告创意文件类型	
					imageJson.addProperty("ftype", image.getFormatId());
					// 素材地址
					imageJson.addProperty("sourceurl", IMAGE_URL + image.getPath());
					// 根据尺寸id查询尺寸信息
					Integer sizeId = image.getSizeId();
					String size = channelSizeClient.selectSizeById(sizeId);
					if (size == null || "".equals(size)) {
						throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
					}

					JsonParser parser = new JsonParser();

					JsonObject jsonObject = parser.parse(size).getAsJsonObject();
					SizeVO sizeVO = new Gson().fromJson(jsonObject.toString(), SizeVO.class);

					// 广告宽度
					imageJson.addProperty("w", sizeVO.getWidth());
					// 广告高度
					imageJson.addProperty("h", sizeVO.getHeight());
					imageJsons.add(imageJson);
					// 最外层的宽、高、素材地址（第一个大图的宽、高、素材地址，如果没有大图则是icon的相关信息）
					if (creativeMaterial.getOrderNo() == 1) {
						// 广告宽度
						creativeObject.addProperty("w", sizeVO.getWidth());
						// 广告高度
						creativeObject.addProperty("h", sizeVO.getHeight());
						// 素材地址
						creativeObject.addProperty("sourceurl", IMAGE_URL + image.getPath());
					}
				}
				if (materialType != null && !materialType.isEmpty() && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_ICON)) {
					ImageModel image = imageMapper.selectByPrimaryKey(materialId);
					JsonObject icon = new JsonObject();
					// 广告创意文件类型	
					icon.addProperty("ftype", image.getFormatId());
					// 素材地址
					icon.addProperty("sourceurl", IMAGE_URL + image.getPath());
					// 根据尺寸id查询尺寸信息
					Integer sizeId = image.getSizeId();
					String size = channelSizeClient.selectSizeById(sizeId);
					if (size == null || "".equals(size)) {
						throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
					}

					JsonParser parser = new JsonParser();

					JsonObject jsonObject = parser.parse(size).getAsJsonObject();
					SizeVO sizeVO = new Gson().fromJson(jsonObject.toString(), SizeVO.class);

					// 广告宽度
					icon.addProperty("w", sizeVO.getWidth());
					// 广告高度
					icon.addProperty("h", sizeVO.getHeight());
					creativeObject.add("icon", icon);
					// 最外层的宽、高、素材地址（第一个大图的宽、高、素材地址，如果没有大图则是icon的相关信息）
					List<CreativeMaterialModel> materials = creativeMaterialMapper.selectByCreativeIdAndType(packageCreativeId,CodeTableConstant.CREATIVE_MATERIAL_IMAGE);
					if (materials == null || materials.isEmpty()) {
						// 如果该创意没有大图，则为icon的信息
						// 广告宽度
						creativeObject.addProperty("w", sizeVO.getWidth());
						// 广告高度
						creativeObject.addProperty("h", sizeVO.getHeight());
						// 素材地址
						creativeObject.addProperty("sourceurl", IMAGE_URL + image.getPath());
					}
				}
			}
			creativeObject.add("imgs", imageJsons);
		}
		
		// 根据物料包id查询物料包信息
		PackageModel packageModel = packageMapper.selectByPrimaryKey(creative.getPackageId());				
		// 点击广告的着陆页
		String landpageUrl = packageModel.getLandpageUrl();
		if (landpageUrl != null && !landpageUrl.isEmpty()) {
			creativeObject.addProperty("landingurl", GlobalUtil.parseString(packageModel.getLandpageUrl(), ""));
		}		
		// 第三方广告展示监测地址
		List<String> tempImonitorUrl = new ArrayList<String>(); 
		String impressionUrl1 = packageModel.getImpressionUrl1();
		if (impressionUrl1 != null && impressionUrl1.isEmpty()) {
			tempImonitorUrl.add(GlobalUtil.parseString(packageModel.getImpressionUrl1(), ""));
		}		
		String impressionUrl2 = packageModel.getImpressionUrl2();
		if (impressionUrl2 != null && impressionUrl2.isEmpty()) {
			tempImonitorUrl.add(GlobalUtil.parseString(packageModel.getImpressionUrl2(), "")); 
		}		
		String[] strTempImonitorUrl = new String[tempImonitorUrl.size()]; //创建一个String型数组
		if (strTempImonitorUrl != null && strTempImonitorUrl.length > 0) {
			tempImonitorUrl.toArray(strTempImonitorUrl); //将list数组转换成String数组
		}		
		JsonArray imoUrlStr = new JsonArray();
		// 第三方广告点击监测地址
		List<String> tempCmonitorUrl = new ArrayList<String>();
		String clickUrl = packageModel.getClickUrl();
		if (clickUrl != null && !clickUrl.isEmpty()) {
			tempCmonitorUrl.add(GlobalUtil.parseString(packageModel.getClickUrl(), ""));
		}		
		JsonArray cmoUrlStr = new JsonArray();
		// 第三方广告监测代码片段
		String monitorcode = "";
		for (int i = 0; i < strTempImonitorUrl.length; i++) {
			imoUrlStr.add(strTempImonitorUrl[i]);
			monitorcode += RedisKeyConstant.MONITOR_TEMPLATES[0].replace("{index}", "" + i).replace("{imonitorurl}", strTempImonitorUrl[i]);
		}
		for (String cmonitorUrl : tempCmonitorUrl) {
			cmoUrlStr.add(cmonitorUrl);
		}
		creativeObject.add("imonitorurl", imoUrlStr);
		creativeObject.add("cmonitorurl", cmoUrlStr);
		creativeObject.addProperty("monitorcode", monitorcode);        
		// Campaign ID
		PolicyModel policy = policyMapper.selectByPrimaryKey(policyCreative.getPolicyId());		
		CampaignModel campagin = campaignMapper.selectByPrimaryKey(policy.getCampaignId());
		if (campagin.getId() != null) {
			creativeObject.addProperty("cid", GlobalUtil.parseString(campagin.getId(), ""));
		}		
		// 广告标题
		creativeObject.addProperty("title", creative.getTitle());
		// 广告描述
		creativeObject.addProperty("description", creative.getDescription());
		// 产品级别
		Integer goodsStar = creative.getGoodsStar();
		if (goodsStar != null) {
			creativeObject.addProperty("rating", creative.getGoodsStar());
		} else {
			creativeObject.addProperty("rating", 255);
		}		
		// CTA描述
		creativeObject.addProperty("ctatext", creative.getCtaDesc());
				
		redisHelper.set(RedisKeyConstant.CREATIVE_INFO + policyCreativeId, creativeObject.toString());
	}
	
	/**
	 * 视频创意信息写入redis
	 * @param policyCreative
	 * @param creative
	 * @throws Exception
	 */
	private void writeVideoCreativeInfo(PolicyCreativeModel policyCreative, CreativeModel creative) throws Exception {
		// 策略-创意关联id
		Integer policyCreativeId = policyCreative.getId();
		// 创意id，即物料包下的创意id
		Integer packageCreativeId = creative.getId();
				
		// 创意基本信息,如下:
		JsonObject creativeObject = new JsonObject();
		// 单个策略下的创意ID
		creativeObject.addProperty("mapid", policyCreativeId);		
		// 创意所属的投放策略ID
		creativeObject.addProperty("policyid", policyCreative.getPolicyId());		
		// 奥丁平台中该创意的ID
		creativeObject.addProperty("creativeid", packageCreativeId);		
		// 广告创意类型
		creativeObject.addProperty("type", 6);
		// 点击目标类型
		creativeObject.addProperty("ctype", 1);
		// 根据物料包id查询物料包信息
		PackageModel packageModel = packageMapper.selectByPrimaryKey(creative.getPackageId());				
		// 点击广告的着陆页
		String landpageUrl = packageModel.getLandpageUrl();
		if (landpageUrl != null && !landpageUrl.isEmpty()) {
			creativeObject.addProperty("landingurl", GlobalUtil.parseString(packageModel.getLandpageUrl(), ""));
		}		
		
		// 根据创意id查询创意-素材信息
		List<CreativeMaterialModel> creativeMaterials = creativeMaterialMapper.selectByCreativeId(packageCreativeId);
		if (creativeMaterials != null && !creativeMaterials.isEmpty()) {
			for (CreativeMaterialModel creativeMaterial : creativeMaterials) {
				Integer materialId = creativeMaterial.getMaterialId();
				String materialType = creativeMaterial.getMaterialType();
				// 根据素材id查询素材信息	
				Date timeLength = null;
				if (materialType != null && !materialType.isEmpty() && materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_VIDEO)) {
					VideoModel video = videoMapper.selectByPrimaryKey(materialId);
					timeLength = video.getUpdateTime();
					// 素材地址
			        creativeObject.addProperty("sourceurl", IMAGE_URL + video.getPath());
					// 广告创意文件类型	
					creativeObject.addProperty("ftype", video.getFormatId());
					// 广告高度
					creativeObject.addProperty("h", video.getHeight());
					// 广告宽度
					creativeObject.addProperty("w", video.getWidth());
				}
				if (materialType.equals(CodeTableConstant.CREATIVE_MATERIAL_IMAGE)) {
					ImageModel image = imageMapper.selectByPrimaryKey(materialId);											
					// 根据策略id查询策略定向信息
					List<PolicyTargetingModel> adxTargets = policyTargetMapper.selectByPolicyIdAndType(policyCreative.getPolicyId(), TargetTypeConstant.ADX_TAGET);
					if (adxTargets != null && !adxTargets.isEmpty()) {						
						JsonArray imageJsons = new JsonArray();
						for (PolicyTargetingModel adxTarget : adxTargets) {
							JsonObject imageObject = new JsonObject();
							String adx = adxTarget.getValue();
							if (adx != null && !adx.isEmpty()) {
								imageObject.addProperty("adx", Integer.valueOf(adx));
							}
							JsonObject ext = new JsonObject();
							ext.addProperty("sourceurl", image.getPath());
							if (timeLength != null) {
								SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
								String duration = sdf.format(timeLength);  
								ext.addProperty("duration", duration);
							}
							imageObject.add("ext", ext);
							imageJsons.add(imageObject);
						}
						creativeObject.add("exts", imageJsons);
					}				
				}
			}
		}
				
		// 第三方广告展示监测地址
		List<String> tempImonitorUrl = new ArrayList<String>();
		String impressionUrl1 = packageModel.getImpressionUrl1();
		if (impressionUrl1 != null && !impressionUrl1.isEmpty()) {
			tempImonitorUrl.add(GlobalUtil.parseString(packageModel.getImpressionUrl1(), ""));
		}		
		String impressionUrl2 = packageModel.getImpressionUrl1();
		if (impressionUrl2 != null && !impressionUrl2.isEmpty()) {
			tempImonitorUrl.add(GlobalUtil.parseString(packageModel.getImpressionUrl2(), ""));
		} 
		String[] strTempImonitorUrl = new String[tempImonitorUrl.size()]; //创建一个String型数组
		if (strTempImonitorUrl != null && strTempImonitorUrl.length > 0) {
			tempImonitorUrl.toArray(strTempImonitorUrl); //将list数组转换成String数组
		}		
		JsonArray imoUrlStr = new JsonArray();
		// 第三方广告点击监测地址
		List<String> tempCmonitorUrl = new ArrayList<String>();
		tempCmonitorUrl.add(GlobalUtil.parseString(packageModel.getClickUrl(), ""));
		JsonArray cmoUrlStr = new JsonArray();
		// 第三方广告监测代码片段
		String monitorcode = "";
		for (int i = 0; i < strTempImonitorUrl.length; i++) {
			imoUrlStr.add(strTempImonitorUrl[i]);
		    monitorcode += RedisKeyConstant.MONITOR_TEMPLATES[0].replace("{index}", "" + i).replace("{imonitorurl}", strTempImonitorUrl[i]);
		}
		for (String cmonitorUrl : tempCmonitorUrl) {
			cmoUrlStr.add(cmonitorUrl);
		}
		creativeObject.add("imonitorurl", imoUrlStr);
		creativeObject.add("cmonitorurl", cmoUrlStr);
		creativeObject.addProperty("monitorcode", monitorcode);        
		// Campaign ID
		PolicyModel policy = policyMapper.selectByPrimaryKey(policyCreative.getPolicyId());
		CampaignModel campagin = campaignMapper.selectByPrimaryKey(policy.getCampaignId());
		if (campagin.getId() != null) {
			creativeObject.addProperty("cid", GlobalUtil.parseString(campagin.getId(), ""));
		}		
		
		redisHelper.set(RedisKeyConstant.CREATIVE_INFO + policyCreativeId, creativeObject.toString());
	}

	/**
	 * 创意的出价信息写入redis : dsp_mapid_bid_(mapid)
	 * @param mapId 策略-创意关联表中的id
	 * @throws Exception
	 */
	public void writeCreateBid(Integer mapId) throws Exception {
		// 查询创意出价等信息
		PolicyCreativeModel policyCreative = policyCreativeMapper.selectByPrimaryKey(mapId);
		if (policyCreative != null) {
			// 出价
			Integer bid = policyCreative.getBid();
			if (bid != null) {
				// 创意的出价
				Map<String, String> creativeBids = new HashMap<String, String>();
				creativeBids.put("all_all_all", bid.toString());
				// 将创意的出价信息写到redis中
				redisHelper.hmset(RedisKeyConstant.CREATIVE_BID + mapId, creativeBids);
			}						
		}
	}
 
	/**
	 * 活动的投放控制策略写入redis : dsp_campaign_control_(campaignid)
	 * @param campaignId
	 * @throws Exception
	 */
	public void writeCampaignControl(Integer campaignId) throws Exception {
		// 如果这个key存在，先将其删除
		removeCampaignControl(campaignId);
		// 根据策略id查询策略信息
		CampaignModel campaign = campaignMapper.selectByPrimaryKey(campaignId);
		if (campaign != null) {
			// 投放控制策略
			Map<String, String> campaignControl = new HashMap<String, String>();
			// 总预算
			Long totalbudget = campaign.getTotalBudget();
			if (totalbudget != null) {
				campaignControl.put("totalbudget", totalbudget.toString());
			} else {
				campaignControl.put("totalbudget", String.valueOf(-1));
			}
			
			// 总展示数
			Long totalimp = campaign.getTotalImpression();
			if (totalimp != null) {
				campaignControl.put("totalimp", totalimp.toString());
			} else {
				campaignControl.put("totalimp", String.valueOf(-1));
			}
			// 总点击数
			Long totalclk = campaign.getTotalClick();
			if (totalclk != null) {
				campaignControl.put("totalclk", totalclk.toString());
			} else {
				campaignControl.put("totalclk", String.valueOf(-1));
			}
			// 根据策略id查询策略KPI信息
			List<CampaignKpiModel> campaignKpis = campaignKpiMapper.selectByCampaignId(campaignId);
			if (campaignKpis != null && !campaignKpis.isEmpty()) {
				for (CampaignKpiModel campaignKpi : campaignKpis) {
					Date day = campaignKpi.getDay();
					SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
					String strDay = sdf.format(day);					
					// 日预算  daybudget_(月日)
					Long dailyBudget = campaignKpi.getDailyBudget();
					if (dailyBudget != null) {
						campaignControl.put("daybudget_" + strDay, dailyBudget.toString());
					}					
					// 日点击上限  dayclk_(月日)
					Long dailyClick = campaignKpi.getDailyClick();
					if (dailyClick != null) {
						campaignControl.put("dayclk_" + strDay, dailyClick.toString());
					}					
					// 日展示上限 dayimp_(月日)
					Long dailyImpression = campaignKpi.getDailyImpression();
					if (dailyImpression != null) {
						campaignControl.put("dayimp_" + strDay, dailyImpression.toString());
					}					
				}
			}						
			
			String cycleType = campaign.getCycleType();
			// 频次控制行为 :redis文档中的Type为0即frequencytype为0======》即无频次控制，即frequencyType为0
			String frequencyType = campaign.getFrequencyType();
			if (frequencyType != null && !frequencyType.isEmpty()) {
				if (frequencyType.equals("0")) {
					// 频次控制类型  
					campaignControl.put("frequencytype", "0");
				} else {
					// 频次控制类型  
					String objectType = campaign.getObjectType();
					if (objectType != null && !objectType.isEmpty()) {
						campaignControl.put("frequencytype", objectType);
					}	
					// 频次控制行为 
					campaignControl.put("frequencyaction", frequencyType);
					// 频次控制周期 					
					if (cycleType != null && !cycleType.isEmpty()) {
						campaignControl.put("frequencyperiod", cycleType);
					}
					// 频次控制数量   
					Integer frequencyAmount = campaign.getFrequencyAmount();
					if (frequencyAmount != null) {
						campaignControl.put("frequencycount", frequencyAmount.toString());
					}	
				}				
			}			
					
			// 当frequencyperiod为全周期时，频次控制结束日期    
			if (cycleType.equals("0")) {
				Date endDate = campaign.getEndDate();
				if (endDate != null) {					
					// 转成时间戳
					long ts = endDate.getTime();
					campaignControl.put("frequencyendtime", String.valueOf(ts));
				}				
			}					
					
			// 将策略信息写入redis中
			redisHelper.hmset(RedisKeyConstant.CAMPAIGN_CONTROL + campaignId.toString(), campaignControl);
		}
	}
	
	/**
	 * 策略的投放控制策略写入redis : dsp_policy_control_(policyid)
	 * @param policyId
	 * @throws Exception
	 */
	public void writePolicyControl(Integer policyId) throws Exception {
		// 如果存在这个key，先将其删除再写入
		removePolicyControl(policyId);
		// 根据策略id查询策略信息
		PolicyModel policy = policyMapper.selectByPrimaryKey(policyId);
		if (policy != null) {
			// 投放控制策略
			Map<String, String> policyControl = new HashMap<String, String>();
			// 是否匀速投放
			String uniform = policy.getIsUniform();
			if (uniform != null && !uniform.isEmpty()) {
				policyControl.put("uniform", uniform);
			}			
			// 总预算
			Long totalbudget = policy.getTotalBudget();
			if (totalbudget != null) {
				policyControl.put("totalbudget", totalbudget.toString());
			} else {
				policyControl.put("totalbudget", String.valueOf(-1));
			}
			// 总展示数
			Long totalimp = policy.getTotalImpression();
			if (totalimp != null) {
				policyControl.put("totalimp", totalimp.toString());
			} else {
				policyControl.put("totalimp", String.valueOf(-1));
			}
			// 总点击数
			Long totalclk = policy.getTotalClick();
			if (totalclk != null) {
				policyControl.put("totalclk", totalclk.toString());
			} else {
				policyControl.put("totalclk", String.valueOf(-1));
			}
			// 根据策略id查询策略KPI信息
			List<PolicyKpiModel> policyKpis = policyKpiMapper.selectByPolicyId(policyId);
			if (policyKpis != null && !policyKpis.isEmpty()) {
				for (PolicyKpiModel policyKpi : policyKpis) {
					Date day = policyKpi.getDay();
					if (day != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
						String strDay = sdf.format(day);
						// 允许的时段 allowtime_(月日)
						Long period = policyKpi.getPeriod();
						if (period != null) {
							policyControl.put("allowtime_" + strDay, period.toString());
						}						
						// 日预算  daybudget_(月日)
						Long dailyBudget = policyKpi.getDailyBudget();
						if (dailyBudget != null) {
							policyControl.put("daybudget_" + strDay, dailyBudget.toString());
						}
						// 日点击上限  dayclk_(月日)
						Long dailyClick = policyKpi.getDailyClick();
						if (dailyClick != null) {
							policyControl.put("dayclk_" + strDay, dailyClick.toString());
						}					
						// 日展示上限 dayimp_(月日)
						Long dailyImpression = policyKpi.getDailyImpression();
						if (dailyImpression != null) {
							policyControl.put("dayimp_" + strDay , dailyImpression.toString());
						}		
					}																		
				}
			}						
			
			String cycleType = policy.getCycleType();
			// 频次控制行为 
			String frequencyType = policy.getFrequencyType();
			if (frequencyType != null && !frequencyType.isEmpty()) {
				if (frequencyType.equals("0")) {
					// 频次控制类型
					policyControl.put("frequencytype", "0");
				} else {
					// 频次控制类型  
					String objectType = policy.getObjectType();
					if (objectType != null && !objectType.isEmpty()) {
						policyControl.put("frequencytype", objectType);
					}
					// 频次控制行为 
					policyControl.put("frequencyaction", frequencyType);
					// 频次控制周期 					
					if (cycleType != null && !cycleType.isEmpty()) {
						policyControl.put("frequencyperiod", cycleType);
					}
					// 频次控制数量   
					Integer frequencyAmount = policy.getFrequencyAmount();
					if (frequencyAmount != null) {
						policyControl.put("frequencycount", frequencyAmount.toString());
					}	
				}				
			}						
			// 当frequencyperiod为全周期时，频次控制结束日期    
			if (cycleType.equals("0")) {
				Date endDate = policy.getEndDate();
				if (endDate != null) {
					// 转成时间戳
					long ts = endDate.getTime();
					policyControl.put("frequencyendtime", String.valueOf(ts));
				}				
			}
					
			
			// 将策略信息写入redis中
			redisHelper.hmset(RedisKeyConstant.POLICY_CONTROL + policyId.toString(), policyControl);
		}
	}	
	
	/**
	 * 向redis写入单个创意id（将创意id写入门到redis的mapids中）
	 * @param policyId
	 * @param mapId 策略-创意关联表中的主键id
	 * @throws Exception
	 */
	public void writeOneCreativeId(Integer policyId, Integer mapId) throws Exception {
		// 取出redis中创意ids
		Set<String> creativeIds = redisHelper.sget(RedisKeyConstant.POLICY_CREATIVEIDS + policyId);
		if (creativeIds != null && !creativeIds.isEmpty()) {
			if (!creativeIds.contains(mapId)) {
				// 如果要写入的创意id不在已在redis的mapids中，则
				redisHelper.sset(RedisKeyConstant.POLICY_CREATIVEIDS + policyId, mapId.toString());
			}
		} else {
			List<String> ids = new ArrayList<String>();
			// 根据策略创意关联表id查询信息
			PolicyCreativeModel policyCreative = policyCreativeMapper.selectByPrimaryKey(mapId);
			// 物料包下的创意id
			Integer pakaageCreativeId = policyCreative.getCreativeId();
			if (creativeService.isOpenPackageCreative(pakaageCreativeId) && 
					creativeService.isOpenPolicyCreative(mapId) && 
					creativeService.isPassAudit(pakaageCreativeId)){
				// 如果物料包下的创意打开 && 策略下的创意打开 && 物料包下创意审核通过，将创意id写入到mapids中
				if (mapId != null) {
					String strId = String.valueOf(mapId);
					ids.add(strId);
				}								
			}		
			// 将创意id写入redis
			redisHelper.addKey(RedisKeyConstant.POLICY_CREATIVEIDS + policyId, ids);
		}
	}
	
	/**
	 * 所有的投放活动ID写入redis : dsp_campaignids
	 * @param campaignId 活动id
	 * @throws Exception
	 */
	public void writeCampaignIds(Integer campaignId) throws Exception {
		if (campaignId != null) {
			String campaignKey = RedisKeyConstant.CAMPAIGN_IDS;
			// 从redis中获取活动ids这个key
			Set<String> redisCampaignIds = redisHelper.sget(campaignKey);
			List<String> campaignIds = new ArrayList<String>();
			String strcampaignId = String.valueOf(campaignId);
			if (redisCampaignIds == null || redisCampaignIds.isEmpty()) {
				// 判断redis中所有正在投放的活动id这个key没有值，将活动id写入
				campaignIds.add(strcampaignId);
			} else {
				if (!redisCampaignIds.contains(strcampaignId)) {
					// 如果redis中存在正在投放的活动id并且这些活动ids中没有包含要写入的活动id，将活动id写入
					campaignIds.add(strcampaignId);
				}
			}
			// 将活动id写入到dsp_campaignids中
			redisHelper.addKey(campaignKey, campaignIds);
		}
	}	
	
	/**
	 * ADX定价合同价格信息写入redis:dsp_fixprice_(adxcode)_ (dealid)
	 * @param contractId
	 * @throws Exception
	 */
//	public void writeAdxContractBidInfo(Integer policyId) throws Exception {
//		if (policyId != null) {
//			// 定价合同定向
//			List<PolicyTargetingModel> contractTargetings = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.CONTRACT_TAGET);
//			if (contractTargetings != null && !contractTargetings.isEmpty()) {
//				for (PolicyTargetingModel contractTargeting : contractTargetings) {
//					String strContractId = contractTargeting.getValue();
//					if (strContractId != null && !strContractId.isEmpty()) {
//						// 定价合同id
//						Integer contractId = Integer.valueOf(strContractId);
//						String contract = channelAdxClient.selectContractById(contractId);
//						if (contract == null || "".equals(contract)) {
//							throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
//						}
//						// 转成对象
//						JsonParser parser = new JsonParser();
//						JsonObject jsonObject = parser.parse(contract).getAsJsonObject();
//						ContractVO contractVO = new Gson().fromJson(jsonObject.toString(), ContractVO.class);
//						// 出价类型
//						String bidType = contractVO.getBidType();
//						// 广告平台ID
//						Integer adxId= contractVO.getAdxId();
//						if (bidType != null && !bidType.isEmpty()) {
//							String fixPriceKey = RedisKeyConstant.FIX_PRICE;
//							Integer bid = contractVO.getBid();
//							Map<String, String> fixPrices = new HashMap<String, String>();
//							if (bidType.equals(CodeTableConstant.BID_TYPE_CPC)) {
//								if (bid != null) {
//									fixPrices.put("clk", String.valueOf(bid));
//								}								
//								fixPrices.put("imp", String.valueOf(-1));
//							} else if (bidType.equals(CodeTableConstant.BID_TYPE_CPM)) {
//								fixPrices.put("clk", String.valueOf(-1));
//								if (bid != null) {
//									fixPrices.put("imp", String.valueOf(bid));
//								}								
//							}
//							redisHelper.hmset(fixPriceKey + adxId + "_" + contractId, fixPrices);
//						}
//					}
//				}
//			}
//		}
//	}
	
	/**
	 * ADX定价合同价格信息写入redis:dsp_fixprice_(adxcode)_ (dealid)
	 * @param contractId
	 * @throws Exception
	 */
	public void writeAdxContractBidInfo(Integer contractId) throws Exception {
		String contract = channelAdxClient.selectContractById(contractId);
		if (contract == null || "".equals(contract)) {
			throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
		}
		// 转成对象
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(contract).getAsJsonObject();
		ContractVO contractVO = new Gson().fromJson(jsonObject.toString(), ContractVO.class);
		// 出价类型
		String bidType = contractVO.getBidType();
		// 广告平台ID
		Integer adxId= contractVO.getAdxId();
		if (bidType != null && !bidType.isEmpty()) {
			String fixPriceKey = RedisKeyConstant.FIX_PRICE;
			Integer bid = contractVO.getBid();
			Map<String, String> fixPrices = new HashMap<String, String>();
			if (bidType.equals(CodeTableConstant.BID_TYPE_CPC)) {
				if (bid != null) {
					fixPrices.put("clk", String.valueOf(bid));
				}								
				fixPrices.put("imp", String.valueOf(-1));
			} else if (bidType.equals(CodeTableConstant.BID_TYPE_CPM)) {
				fixPrices.put("clk", String.valueOf(-1));
				if (bid != null) {
					fixPrices.put("imp", String.valueOf(bid));
				}								
			}
			String key = fixPriceKey + adxId + "_" + contractId; 
			if (fixPrices != null && !fixPrices.isEmpty()) {
				redisHelper.hmset(key, fixPrices);
			}			
		}
	}
	
	/**
	 * 广告信息设置更新时间标识写入redis
	 * @param time
	 * @throws Exception
	 */
	public void writeAdsettingWorking() throws Exception {
		String adsettingWorking = RedisKeyConstant.ADSETTING_WORKING;
		redisHelper.set(adsettingWorking, new Date().getTime());		
	}
	
	/**
	 * 将策略id从 策略ids中删除:所有的投放策略ID即dsp_policyids
	 * @param policyId
	 * @return
	 * @throws Exception
	 */
	public void removePolicyId(Integer policyId) throws Exception {
		String strPolicyKey = RedisKeyConstant.POLICY_IDS;
		// 从redis中获取策略ids这个key 
		Set<String> strPolicyIds = redisHelper.sget(RedisKeyConstant.POLICY_IDS);
		String strPolicyId = String.valueOf(policyId);
		if (strPolicyIds != null && !strPolicyIds.isEmpty()) {
			if (strPolicyIds.contains(strPolicyId)) {
				// 如果要删除的策略id在策略ids中则将其删除
				redisHelper.sdelete(strPolicyKey, strPolicyId);
			}
		}
	}
	
	/**
	 * 策略下的创意id移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removeCreativeIds(Integer policyId) throws Exception {
		redisHelper.delete(RedisKeyConstant.POLICY_CREATIVEIDS + policyId);
	}
	
	/**
	 * 将策略基本信息移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removePolicyInfo(Integer policyId) throws Exception {
		redisHelper.delete(RedisKeyConstant.POLICY_INFO + policyId);
	}
	
	/**
	 * 策略定向移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removePolicyTarget(Integer policyId) throws Exception {
		redisHelper.delete(RedisKeyConstant.POLICY_TARGET + policyId);
	}
	
	/**
	 * 将黑白名单移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removeWhiteBlack(Integer policyId) throws Exception {
		
	}
	
	/**
	 * 创意基本信息移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removeCreativeInfo(Integer policyId) throws Exception {
		// 查询策略下的创意
		List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(policyId);
		if (policyCreatives != null && !policyCreatives.isEmpty()) {
			// 将策略下所有的创意基本信息从redis中删除
			for (PolicyCreativeModel policyCreative : policyCreatives) {
				redisHelper.delete(RedisKeyConstant.CREATIVE_INFO + policyCreative.getId());
			}
		}
	}
	
	/**
	 * 创意出价信息移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removeCreativeBid(Integer policyId) throws Exception {
		// 查询策略下的创意
		List<PolicyCreativeModel> policyCreatives = policyCreativeMapper.seleceByPolicyId(policyId);
		if (policyCreatives != null && !policyCreatives.isEmpty()) {
			// 将策略下所有的创意出价信息从redis中删除
			for (PolicyCreativeModel policyCreative : policyCreatives) {
				redisHelper.delete(RedisKeyConstant.CREATIVE_BID + policyCreative.getId());
			}
		}
	}
	
	/**
	 * 活动的投放控制策略移除redis
	 * @param campaignId
	 * @throws Exception
	 */
	public void removeCampaignControl(Integer campaignId) throws Exception {
		redisHelper.hdelete(RedisKeyConstant.CAMPAIGN_CONTROL + campaignId);
	}
	
	/**
	 * 策略的投放控制策略移除redis
	 * @param policyId
	 * @throws Exception
	 */
	public void removePolicyControl(Integer policyId) throws Exception {
		redisHelper.hdelete(RedisKeyConstant.POLICY_CONTROL + policyId);
	}
	
	/**
	 * 从redis中删除策略下一个创意id
	 * @param policyId
	 * @param mapId 策略下的创意id
	 * @throws Exception
	 */
	public void removeOneCreativeId(Integer policyId, Integer mapId) throws Exception {
		// 取出redis中创意ids
		Set<String> creativeIds = redisHelper.sget(RedisKeyConstant.POLICY_CREATIVEIDS + policyId);
		if (creativeIds != null && !creativeIds.isEmpty()) {
			if (creativeIds.contains(mapId.toString())) {
				// 如果要写入的创意id在已在redis的mapids中，则将其移除
				redisHelper.sdelete(RedisKeyConstant.POLICY_CREATIVEIDS + policyId, mapId.toString());
			}
		}
	}
	
	/**
	 * 将活动id从 活动ids中删除:所有的投放活动ID即dsp_campaignids
	 * @param campaignId 活动id
	 * @throws Exception
	 */
	public void removeCampaignId(Integer campaignId) throws Exception {
		String campaignKey = RedisKeyConstant.CAMPAIGN_IDS;
		if (campaignId != null) {
			// 获取redis中活动ids
			Set<String> campaignIds = redisHelper.sget(campaignKey);
			// 将活动的id转成String类型
			String strCampaignId = String.valueOf(campaignId);
			// 判断要删除的活动id是否在campaignids中
			if (campaignIds.contains(strCampaignId)) {
				// 如果要删除的活动id在活动ids中则将其删除
				redisHelper.sdelete(campaignKey, strCampaignId);
			}
		}
	}
	
	/**
	 * ADX定价合同价格信息移除redis,dsp_fixprice_(adxcode)_ (dealid)
	 * @param policyId
	 * @throws Exception
	 */
//	public void removeAdxContractBidInfo(Integer policyId) throws Exception {
//		if (policyId != null) {
//			// 定价合同定向
//			List<PolicyTargetingModel> contractTargetings = policyTargetMapper.selectByPolicyIdAndType(policyId, TargetTypeConstant.CONTRACT_TAGET);
//			if (contractTargetings != null && !contractTargetings.isEmpty()) {
//				for (PolicyTargetingModel contractTargeting : contractTargetings) {
//					String strContractId = contractTargeting.getValue();
//					if (strContractId != null && !strContractId.isEmpty()) {
//						// 定价合同id
//						Integer contractId = Integer.valueOf(strContractId);
//						String contract = channelAdxClient.selectContractById(contractId);
//						if (contract == null || "".equals(contract)) {
//							throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
//						}
//						// 转成对象
//						JsonParser parser = new JsonParser();
//						JsonObject jsonObject = parser.parse(contract).getAsJsonObject();
//						ContractVO contractVO = new Gson().fromJson(jsonObject.toString(), ContractVO.class);
//						// 广告平台ID
//						Integer adxId= contractVO.getAdxId();
//						// 策略下的定价合同信息从redis中删除
//						redisHelper.hdelete(RedisKeyConstant.FIX_PRICE + adxId + "_" + contractId);
//					}
//				}
//			}
//		}
//	}
	
	/**
	 * ADX定价合同价格信息移除redis,dsp_fixprice_(adxcode)_ (dealid)
	 * @param contractId
	 * @throws Exception
	 */
	public void removeAdxContractBidInfo(Integer contractId) throws Exception {
		String contract = channelAdxClient.selectContractById(contractId);
		if (contract == null || "".equals(contract)) {
			throw new DuplicateEntityException(PhrasesConstant.GET_ADX_INFO_FAILED);
		}
		// 转成对象
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(contract).getAsJsonObject();
		ContractVO contractVO = new Gson().fromJson(jsonObject.toString(), ContractVO.class);
		// 广告平台ID
		Integer adxId= contractVO.getAdxId();
		// 策略下的定价合同信息从redis中删除
		redisHelper.hdelete(RedisKeyConstant.FIX_PRICE + adxId + "_" + contractId);
	}
}
