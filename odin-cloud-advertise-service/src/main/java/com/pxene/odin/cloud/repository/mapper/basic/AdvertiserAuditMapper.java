package com.pxene.odin.cloud.repository.mapper.basic;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.AdvertiserAuditModel;

@Mapper
public interface AdvertiserAuditMapper {

	@Select("select advertiser_id as advertiserId,adx_id as adxId,audit_value as auditValue,status,message,"
			+ "response,create_user as createUser,create_time as createTime,update_user as updateUser,"
			+ "update_time as updateTime from tb_advertiser_audit where advertiser_id = #{advertiserId} "
			+ "and adx_id = #{adxId}")
	AdvertiserAuditModel selectByPrimaryKey(@Param("advertiserId")Integer advertiserId, @Param("adxId")Integer adxId);
}
