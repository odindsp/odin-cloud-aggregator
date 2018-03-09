package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.pxene.odin.cloud.domain.model.AppModel;

@Mapper
public interface AppMapper {

	@SelectProvider(method = "selectAppByAdxIdAndName", type = AppProvider.class)
	List<AppModel> selectAppByAdxIdAndName(Map<String,Object>params);
	
	@SelectProvider(method = "selectAppByAdxIdAndAppId", type = AppProvider.class)
	List<AppModel> selectAppByAdxIdAndAppId(@Param("adxIds")Set<String> adxIds, @Param("appIds")Set<String> appIds);
}
