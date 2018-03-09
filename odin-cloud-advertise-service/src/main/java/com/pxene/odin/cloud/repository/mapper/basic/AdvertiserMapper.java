package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.pxene.odin.cloud.domain.model.AdvertiserModel;

@Mapper
public interface AdvertiserMapper {
	
	@Options(useGeneratedKeys=true)
	@Insert("insert into tb_advertiser (name,company_name,is_protected,contacts,contact_num,email,"
			+ "qq,industry_id,brand,logo_path,account_licence_path,business_licence_path,"
			+ "organization_code_path,icp_path,licence_no,licence_deadline,organization_code,"
			+ "telephone,address,zip,website_url,website_name,saleman,create_user,update_user)"
			+ " values (#{name},#{companyName},#{isProtected},#{contacts},"
			+ "#{contactNum},#{email},#{qq},#{industryId},#{brand},#{logoPath},"
			+ "#{accountLicencePath},#{businessLicencePath},#{organizationCodePath},"
			+ "#{icpPath},#{licenceNo},#{licenceDeadline},#{organizationCode},#{telephone},#{address},"
			+ "#{zip},#{websiteUrl},#{websiteName},#{saleman},#{createUser},#{updateUser})")
	int insert(AdvertiserModel advertiser);
	
	@Select("select id,name,company_name as companyName,is_protected as isProtected,contacts,"
			+ "contact_num as contactNum,email,qq,industry_id as industryId,brand,logo_path as logoPath,"
			+ "account_licence_path as accountLicencePath,business_licence_path as businessLicencePath,"
			+ "organization_code_path as organizationCodePath,icp_path as icpPath,licence_no as licenceNo,"
			+ "licence_deadline as licenceDeadline,organization_code as organizationCode,telephone,"
			+ "address,zip,website_url as websiteUrl,website_name as websiteName,saleman"
			+ " from tb_advertiser where name = #{name}")
	AdvertiserModel selectAdvertiserByName(@Param("name")String name); 
	
	@Select("select id,name,company_name as companyName,is_protected as isProtected,contacts,"
			+ "contact_num as contactNum,email,qq,industry_id as industryId,brand,logo_path as logoPath,"
			+ "account_licence_path as accountLicencePath,business_licence_path as businessLicencePath,"
			+ "organization_code_path as organizationCodePath,icp_path as icpPath,licence_no as licenceNo,"
			+ "licence_deadline as licenceDeadline,organization_code as organizationCode,telephone,"
			+ "address,zip,website_url as websiteUrl,website_name as websiteName,saleman"
			+ " from tb_advertiser where id = #{id}")
	AdvertiserModel selectByPrimaryKey(@Param("id")Integer id);
	
	@Update("update tb_advertiser set company_name = #{companyName}, is_protected = #{isProtected}, "
			+ "contacts = #{contacts}, contact_num = #{contactNum}, email = #{email}, qq = #{qq}, "
			+ "industry_id = #{industryId}, brand = #{brand}, logo_path = #{logoPath}, "
			+ "account_licence_path = #{accountLicencePath}, business_licence_path = #{businessLicencePath}, "
			+ "organization_code_path = #{organizationCodePath}, icp_path = #{icpPath}, licence_no = #{licenceNo}, "
			+ "licence_deadline = #{licenceDeadline}, organization_code = #{organizationCode}, telephone = #{telephone}, "
			+ "address = #{address}, zip = #{zip}, website_url = #{websiteUrl}, website_name = #{websiteName}, "
			+ "saleman = #{saleman}, update_user = #{updateUser} where id = #{id}")
	int updateByPrimaryKey(AdvertiserModel advertiser);
	
	@SelectProvider(method = "selectAdvertiser", type = AdvertiserProvider.class)
	List<AdvertiserModel> selectAdvertiser(Map<String,Object> record);
	
	@Select("select * from tb_advertiser where company_name = #{companyName}")
	AdvertiserModel selectByCompanyName(@Param("companyName")String companyName);
	
	@Select("select * from tb_advertiser where company_name = #{companyName} and id != #{id}")
	AdvertiserModel selectByCompanyNameAndNotId(@Param("companyName")String companyName, @Param("id")Integer id);
}
