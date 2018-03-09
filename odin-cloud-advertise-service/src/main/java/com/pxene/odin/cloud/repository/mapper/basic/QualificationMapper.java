package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.QualificationModel;

@Mapper
public interface QualificationMapper {
	
	@Insert("insert into tb_qualification (advertiser_id, type, path,create_user,update_user) "
			+ "values (#{advertiserId}, #{type}, #{path}, #{createUser}, #{updateUser})")
	int insert(QualificationModel qualification);
	
	@Select("select advertiser_id as advertiserId,type, path from tb_qualification"
			+ " where advertiser_id = #{advertiserId}")
	List<QualificationModel> selectQualificationByAdvertiserId(@Param("advertiserId")Integer advertiserId);
	
	@Delete("delete from tb_qualification where advertiser_id = #{advertiserId}")
	int deleteQualificationByAdvertiserId(@Param("advertiserId")Integer advertiserId);
}
