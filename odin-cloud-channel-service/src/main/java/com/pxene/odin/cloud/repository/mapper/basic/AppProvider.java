package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * AppProvider类
 * @author lizhuoling
 *
 */
public class AppProvider {

	/**
	 * 根据ADXId和媒体名称查询媒体基本信息
	 * @param params 参数
	 * @return
	 */
	public String selectAppByAdxIdAndName(Map<String, Object>params) {
		// 参数
		StringBuffer sql = new StringBuffer();
		Integer adxId = (Integer)params.get("adxId");
		String name = (String)params.get("name");
		String searchType = (String)params.get("searchType");
		
		// 拼接动态sql:如果ADXId等于0，则表示全部ADX
		sql.append("select id,adx_id as adxId,name,pkg_name as pkgName,");
		sql.append("download_url as downloadUrl,os_type as osType,type_id as typeId from tb_app where 1=1");
		
		// 如果ADXId不为0，则根据ADXid查询  
		if (adxId != 0) {
			sql.append(" and adx_id = '" + adxId + "'");
		}
		
		// 根据媒体名称精确查询
		if (StringUtils.isNotBlank(name) && searchType.equals("1")) {
			sql.append(" and name = '" + name + "'");
		}
		
		// 根据媒体名称模糊查询
		if (StringUtils.isNotBlank(name) && searchType.equals("0")) {
			sql.append(" and name like '%" + name + "%'");
		}
		
		// 返回前一万条数据
		sql.append(" limit 10000");
		
		return sql.toString();
	}
	
	public String selectAppByAdxIdAndAppId(Map<String, Object>params) {
		// 参数
		StringBuffer sql = new StringBuffer();
		Set<String> adxIds = (Set<String>)params.get("adxIds");
		Set<String> appIds = (Set<String>)params.get("appIds");
		// 拼接动态sql:如果ADXId等于0，则表示全部ADX
		sql.append("select id,adx_id as adxId,name,pkg_name as pkgName,");
		sql.append("download_url as downloadUrl,os_type as osType, type_id as typeId from tb_app where 1=1");				
		// 如果ADXId不为0，则根据ADXid查询  
		if (adxIds != null && !adxIds.isEmpty()) {
			// list集合转成用逗号分隔的字符串
			String adxId = StringUtils.join(adxIds.toArray(), ",");
			sql.append(" and adx_id in (" + adxId + ")");
		}
		if (appIds != null && !appIds.isEmpty()) {
			// list集合转成用逗号分隔的字符串
			String appId = StringUtils.join(appIds.toArray(), ",");
			sql.append(" and id in (" + appId + ")");
		}
		return sql.toString();
	}
}
