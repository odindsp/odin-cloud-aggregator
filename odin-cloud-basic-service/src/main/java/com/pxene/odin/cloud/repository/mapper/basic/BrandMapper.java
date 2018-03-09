package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.BrandModel;

@Mapper
public interface BrandMapper {

	@Select("select id, cn_name as cnName, en_name as enName, grade from td_brand")
	List<BrandModel> selectBrands();
}
