package com.pxene.odin.cloud.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.common.PhrasesConstant;
import com.pxene.odin.cloud.domain.model.AppModel;
import com.pxene.odin.cloud.domain.model.AppTypeAdxModel;
import com.pxene.odin.cloud.domain.model.AppTypeModel;
import com.pxene.odin.cloud.domain.vo.AppTypeVO;
import com.pxene.odin.cloud.domain.vo.AppVO;
import com.pxene.odin.cloud.repository.mapper.basic.AdxMapper;
import com.pxene.odin.cloud.repository.mapper.basic.AppMapper;
import com.pxene.odin.cloud.repository.mapper.basic.AppTypeAdxMapper;
import com.pxene.odin.cloud.repository.mapper.basic.AppTypeMapper;

@Service
@Transactional
public class AppService extends BaseService{
	
	@Autowired
	private AppTypeMapper appTypeMapper;
	
	@Autowired
	private AdxMapper adxMapper;
	
	@Autowired
	private AppMapper appMapper;
	
	@Autowired
	private AppTypeAdxMapper appTypeAdxMapper;
	
	/**
	 * 批量查询媒体类型
	 * @return
	 */
	public List<AppTypeVO> listAppTypes() {
		// 查询媒体分类
		List<AppTypeModel> appTypesModel = appTypeMapper.selectAppTypes();
		// 将媒体分类信息复制到VO中
		List<AppTypeVO> appTypes = new ArrayList<AppTypeVO>();
		if (appTypesModel != null && !appTypesModel.isEmpty()) {
			// 如果媒体类型信息不为空
			for (AppTypeModel appTypeModel : appTypesModel) {
				AppTypeVO appType = modelMapper.map(appTypeModel, AppTypeVO.class);
				appType.setName(appTypeModel.getOneLevelName());
				appTypes.add(appType);
			}
		}		
		return appTypes;
	}
	
	/**
	 * 批量查询媒体（媒体即APP）
	 * @param adxId 广告平台ID
	 * @param name 媒体名称即App名称
	 * @param searchType 搜索类型
	 * @return
	 */
	public List<AppVO> listApps(Integer adxId, String[] names, String searchType) {						
		// 查询媒体基本信息
		List<AppModel> appsModel = new ArrayList<AppModel>();
		Map<String,Object> appMap = new HashMap<>();
		appMap.put("adxId", adxId);
		if (names != null && names.length > 0 && searchType != null && !searchType.isEmpty()) {
			appMap.put("searchType", searchType);
			// 去重
			Set<AppModel> setApps = new HashSet<AppModel>();
			for (String strName : names) {
				appMap.put("name", strName);		
				List<AppModel> apps = appMapper.selectAppByAdxIdAndName(appMap);
				if (apps != null && !apps.isEmpty()) {
					for (AppModel app : apps) {
						setApps.add(app);
					}
				}
			}
			appsModel.addAll(setApps);
		} else {
			appsModel = appMapper.selectAppByAdxIdAndName(appMap);
		}				
		List<AppVO> apps = new ArrayList<AppVO>();
		if (appsModel != null && !appsModel.isEmpty()) {
			for (AppModel appModel : appsModel) {
				// 将媒体基本信息复制到对应的VO中
				AppVO app = modelMapper.map(appModel, AppVO.class);
				app.setId(appModel.getId());
				app.setName(appModel.getName());
				app.setOs(appModel.getOsType());
				app.setPackageName(appModel.getPkgName());
				// 查询ADX的名称 
				Integer adx = appModel.getAdxId();
				String adxName = adxMapper.selectNameByPrimaryKey(adx);
				app.setAdxName(adxName);
				// 查询媒体分类名称
				Integer appTypeId = appModel.getTypeId();
				AppTypeModel appType = appTypeMapper.selectAppTypeNameByPrimaryKey(appTypeId);
				if (appType != null) {
					app.setOneLevelName(appType.getOneLevelName());
					app.setTwoLevelName(appType.getTwoLevelName());
				}				
				// 查询媒体分类code
				AppTypeAdxModel appTypeAdx = appTypeAdxMapper.selectCodeByPrimaryKey(appTypeId, adx);
				if (appTypeAdx != null) {
					app.setOneLevelCode(appTypeAdx.getOneLevelCode());
					app.setTwoLevelCode(appTypeAdx.getTwoLevelCode());
				}				
				apps.add(app);
			}
		}
		// 返回媒体信息
		return apps;		
	}
	
	/**
	 * 根据ID批量查询媒体
	 * @param ids：Adx Id和App Id
	 * @return
	 */
	public List<AppVO> listAppsByIds(String[] ids) {
		if (ids.length == 0) {
			List<AppVO> apps = new ArrayList<AppVO>();
			return apps;
		}
		// Adx Id和App Id
		Set<String> adxIds = new HashSet<String>();
		Set<String> appIds = new HashSet<String>();
		for (String id : ids) {
			if (id.contains("|")) {
				String[] result = id.split("\\|");
				String adxId = result[0];
				adxIds.add(adxId);
				String appId = result[1];
				appIds.add("\"" + appId + "\"");
			}			
		}
		// 根据ID批量查询媒体信息
		List<AppModel> appsModel = appMapper.selectAppByAdxIdAndAppId(adxIds,appIds);
		List<AppVO> apps = new ArrayList<AppVO>();
		if (appsModel != null && !appsModel.isEmpty()) {
			for (AppModel appModel : appsModel) {
				// 将媒体基本信息复制到对应的VO中  
				AppVO app = modelMapper.map(appModel, AppVO.class);
				app.setId(appModel.getId());
				app.setName(appModel.getName());
				app.setOs(appModel.getOsType());
				app.setPackageName(appModel.getPkgName());
				// 查询ADX的名称 
				Integer adx = appModel.getAdxId();
				String adxName = adxMapper.selectNameByPrimaryKey(adx);
				app.setAdxName(adxName);
				// 查询媒体分类名称
				Integer appTypeId = appModel.getTypeId();
				AppTypeModel appType = appTypeMapper.selectAppTypeNameByPrimaryKey(appTypeId);
				if (appType != null) {
					app.setOneLevelName(appType.getOneLevelName());
					app.setTwoLevelName(appType.getTwoLevelName());
				}				
				// 查询媒体分类code
				AppTypeAdxModel appTypeAdx = appTypeAdxMapper.selectCodeByPrimaryKey(appTypeId, adx);
				if (appTypeAdx != null) {
					app.setOneLevelCode(appTypeAdx.getOneLevelCode());
					app.setTwoLevelCode(appTypeAdx.getTwoLevelCode());
				}				
				apps.add(app);
			}
		}
		// 返回媒体信息
		return apps;		
	}

}
