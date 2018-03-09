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

import com.pxene.odin.cloud.domain.model.PackageModel;

@Mapper
public interface PackageMapper {

	@Options(useGeneratedKeys = true)
	@Insert("insert into tb_package (name,campaign_id,impression_url1,impression_url2,click_url,landpage_url,"
			+ "is_landpage_code,deeplink_url,create_user,update_user) values (#{name},#{campaignId},#{impressionUrl1},"
			+ "#{impressionUrl2},#{clickUrl},#{landpageUrl},#{isLandpageCode},#{deeplinkUrl},#{createUser},#{updateUser})")
	int insert (PackageModel packageModel);
	
	@Select("select id,name,campaign_id as campaignId,impression_url1 as impressionUrl1,impression_url2 as impressionUrl2,"
			+ "click_url as clickUrl,landpage_url as landpageUrl,is_landpage_code as isLandpageCode,"
			+ "deeplink_url as deeplinkUrl from tb_package where id = #{id}")
	PackageModel selectByPrimaryKey(@Param("id")Integer id);
	
	@Update("update tb_package set name = #{name}, impression_url1 = #{impressionUrl1}, impression_url2 = #{impressionUrl2},"
			+ "click_url = #{clickUrl}, landpage_url = #{landpageUrl}, is_landpage_code = #{isLandpageCode},"
			+ "deeplink_url = #{deeplinkUrl}, campaign_id = #{campaignId},update_user = #{updateUser} where id = #{id}")
	void updateByPrimaryKey(PackageModel packageModel);
	
	@SelectProvider(method = "selectAllPackage", type = PackageProvider.class)
	List<PackageModel> selectAllPackage(Map<String,Object> params);
	
	@Select("select id,name,campaign_id as campaignId,impression_url1 as impressionUrl1,impression_url2 as impressionUrl2,"
			+ "click_url as clickUrl,landpage_url as landpageUrl,is_landpage_code as isLandpageCode,"
			+ "deeplink_url as deeplinkUrl from tb_package where name = #{name} and campaign_id = #{campaignId}")
	List<PackageModel> selectByNameAndCampaignId(@Param("name") String name,@Param("campaignId") Integer campaignId);
	
	@SelectProvider(method = "selectByNameAndCampaignIdAndNotId", type = PackageProvider.class)
	List<PackageModel> selectByNameAndCampaignIdAndNotId(Map<String,Object> params);
	
	@Select({
        "select count(*) from tb_package where campaign_id = #{campaignId,jdbcType=INTEGER}"
    })
	Integer selectPackageCountByCampaignId(@Param("campaignId") Integer campaignId);
}
