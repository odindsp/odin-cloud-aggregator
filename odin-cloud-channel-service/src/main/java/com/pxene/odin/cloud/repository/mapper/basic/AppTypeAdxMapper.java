package com.pxene.odin.cloud.repository.mapper.basic;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.AppTypeAdxModel;

@Mapper
public interface AppTypeAdxMapper {
	
	@Select("select one_level_code as oneLevelCode,two_level_code as twoLevelCode,three_level_code "
			+ "as threeLevelCode from tb_app_type_adx where app_type_id = #{appTypeId} "
			+ "and adx_id = #{adxId}")
	AppTypeAdxModel selectCodeByPrimaryKey(@Param("appTypeId")Integer appTypeId,@Param("adxId")Integer adxId);
}
