package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class AdvertiserProvider {
	/**
	 * 批量查询广告主
	 * @param params
	 * @return
	 */
	public String selectAdvertiser(Map<String, Object>params) {
		StringBuffer sql = new StringBuffer();
		String name = (String)params.get("name");
		String contacts = (String)params.get("contacts");
		String companyName = (String)params.get("companyName");
		
		sql.append("select id,name,company_name as companyName,is_protected as isProtected,contacts,");
		sql.append("contact_num as contactNum,email,qq,industry_id as industryId,brand,logo_path as logoPath,");
		sql.append("account_licence_path as accountLicencePath,business_licence_path as businessLicencePath,");
		sql.append("organization_code_path as organizationCodePath,icp_path as icpPath,licence_no as licenceNo,");
		sql.append("licence_deadline as licenceDeadline,organization_code as organizationCode,telephone,");
		sql.append("address,zip,website_url as websiteUrl,website_name as websiteName,saleman ");
		sql.append("from tb_advertiser where 1=1 ");
		
		if (StringUtils.isNotBlank(name)) {
			sql.append(" and name like '%" + name + "%'");
		}
		
		if (StringUtils.isNotBlank(contacts)) {
			sql.append(" and contacts like '%" + contacts + "%'");
		}
		
		if (StringUtils.isNotBlank(companyName)) {
			sql.append(" and company_name like '%" + companyName + "%'");
		}
		return sql.toString();
	}
}
