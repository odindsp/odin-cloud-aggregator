package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.AdxModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdxMapper {

	@Select("select name from tb_adx where id = #{id}")
	String selectNameByPrimaryKey(@Param("id")Integer id);
		
	@Select("select logo_path as logoPath from tb_adx where id = #{id}")
	String selectLogoPathByPrimaryKey(@Param("id")Integer id);
	
	@Select("select id,name,logo_path as logoPath from tb_adx")
	List<AdxModel> selectAdxs();

	@Select("SELECT id, `name`, logo_path AS logoPath, exchange_rate AS exchangeRate,"
			+ " need_advertiser_audit AS needAdvertiserAudit, need_creative_audit AS needCreativeAudit,"
			+ " support_ssl AS supportSsl, `enable`, iurl, cturl, aurl, nurl, andr_image_tmpl AS andrImageTmpl,"
			+ " ios_image_tmpl AS iosImageTmpl, andr_video_tmpl AS andrVideoTmpl, ios_video_tmpl AS iosVideoTmpl,"
			+ " andr_infoflow_tmpl AS andrInfoflowTmpl, ios_infoflow_tmpl AS iosInfoflowTmpl,"
			+ " security_key AS securityKey"
			+ " FROM tb_adx where id = #{id,jdbcType=INTEGER}")
	AdxModel selectByPrimaryKey(Integer id);
}
