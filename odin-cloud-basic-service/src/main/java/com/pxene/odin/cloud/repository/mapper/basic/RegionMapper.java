package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.RegionModel;

@Mapper
public interface RegionMapper {
	
	@Select("select * from td_region")
	List<RegionModel> selectRegions();
}
