package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.SizeModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SizeMapper {

  @Select("select id,width,height,adtype_id as adtypeId ,create_user as createUser,create_time as createTime"
      + ",update_user as updateUser,update_time updateTime from tb_size")
  List<SizeModel> selectSizes();

  @Select("select id,width,height,adtype_id as adtypeId ,create_user as createUser,create_time as createTime"
      + ",update_user as updateUser,update_time updateTime from tb_size where id = #{id,jdbcType=INTEGER} ")
  SizeModel selectByPrimaryKey(@Param("id")Integer id);
}
