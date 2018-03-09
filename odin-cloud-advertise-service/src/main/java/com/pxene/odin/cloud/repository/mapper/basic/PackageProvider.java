package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 物料包Provider
 * @author lizhuoling
 *
 */
public class PackageProvider {
	/**
	 * 批量查询物料包
	 * @param params
	 * @return
	 */
	public String selectAllPackage(Map<String, Object>params) {
		StringBuffer sql = new StringBuffer();
		Integer campaginId = (Integer)params.get("campaginId");
		
		sql.append("select id,name,campaign_id as campaignId,");
		sql.append("impression_url1 as impressionUrl1,impression_url2 as impressionUrl2,");
		sql.append("click_url as clickUrl,landpage_url as landpageUrl,is_landpage_code as isLandpageCode,");
		sql.append("deeplink_url as deeplinkUrl from tb_package");
		
		if (campaginId != null) {
			sql.append(" where campaign_id ='" + campaginId + "'");
		}
		
		return sql.toString();
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	public String selectByNameAndCampaignIdAndNotId(Map<String, Object> params) {
		StringBuffer sql = new StringBuffer();
		Integer id = (Integer)params.get("id");
		Integer campaginId = (Integer)params.get("campaignId");
		String name = (String)params.get("name");
		
		sql.append("select id,name,campaign_id as campaignId,");
		sql.append("impression_url1 as impressionUrl1,impression_url2 as impressionUrl2,");
		sql.append("click_url as clickUrl,landpage_url as landpageUrl,is_landpage_code as isLandpageCode,");
		sql.append("deeplink_url as deeplinkUrl from tb_package where id != '" + id + "'");
		
		if (StringUtils.isNotBlank(name)) {
			sql.append(" and name ='" + name + "'");
		}
		
		if (campaginId != null) {
			sql.append(" and campaign_id ='" + campaginId + "'");
		}
		
		return sql.toString();
	}
}
