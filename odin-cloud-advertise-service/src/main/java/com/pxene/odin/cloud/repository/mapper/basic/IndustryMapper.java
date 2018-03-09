package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IndustryMapper {

	@Select({ "select id, name from tb_industry" })
	List<Map<String, Object>> findAllIndustrys();
	
	@Select("select name from tb_industry where id=#{id,jdbcType=INTEGER};")
	String findIndustryNameById(@Param("id") Integer id);
}