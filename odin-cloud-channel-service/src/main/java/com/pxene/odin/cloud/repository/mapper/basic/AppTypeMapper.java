package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.AppTypeModel;

@Mapper
public interface AppTypeMapper {
	
	/**
	 * 查询媒体类型
	 * @return
	 */
	@Select("select id,one_level_name as oneLevelName from tb_app_type where (three_level_name = '' OR three_level_name IS NULL) "
			+ "AND (two_level_name = '' OR two_level_name IS NULL)")
	List<AppTypeModel> selectAppTypes();
	
	/**
	 * 根据ADXId查询媒体名称
	 * @param id
	 * @return
	 */
	@Select("select id,one_level_name as oneLevelName,two_level_name as twoLevelName,"
			+ "three_level_name as threeLevelName from tb_app_type where id = #{id}")
	AppTypeModel selectAppTypeNameByPrimaryKey(@Param("id")Integer id);
}
