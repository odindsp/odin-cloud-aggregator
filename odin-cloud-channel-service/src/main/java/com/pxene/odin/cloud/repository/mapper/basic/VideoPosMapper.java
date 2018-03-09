package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.VideoPosModel;
import java.util.List;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

public interface VideoPosMapper {


  @Select({
      "select",
      "id, name, code, adx_id, size_id, duration, max_volume, need_image, ",
      "image_max_volume, create_user, create_time, update_user, update_time",
      "from tb_video_pos",
      "where id = #{id,jdbcType=INTEGER}"
  })
  @Results({
      @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
      @Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
      @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
      @Result(column = "adx_id", property = "adxId", jdbcType = JdbcType.INTEGER),
      @Result(column = "size_id", property = "sizeId", jdbcType = JdbcType.INTEGER),
      @Result(column = "size_id", property = "frameWidth",one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectSizeWidthById")),
      @Result(column = "size_id", property = "frameHeight", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectSizeHeightById")),
      @Result(column = "duration", property = "duration", jdbcType = JdbcType.INTEGER),
      @Result(column = "max_volume", property = "maxVolume", jdbcType = JdbcType.INTEGER),
      @Result(column = "need_image", property = "needImage", jdbcType = JdbcType.VARCHAR),
      @Result(column = "image_max_volume", property = "imageMaxVolume", jdbcType = JdbcType.INTEGER),
      @Result(column = "create_user", property = "createUser", jdbcType = JdbcType.INTEGER),
      @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "update_user", property = "updateUser", jdbcType = JdbcType.INTEGER),
      @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP),
      @Result(column = "id", property = "videoFormats", many = @Many(select = "com.pxene.odin.cloud.repository.mapper.basic.VideoPosMapper.selectVideoPosFormatByid"))
  })
  VideoPosModel selectByPrimaryKey(Integer id);

  @Select({
      "select",
      "id, name, code, adx_id, size_id, duration, max_volume, need_image, ",
      "image_max_volume, create_user, create_time, update_user, update_time",
      "from tb_video_pos"
  })
  @Results({
      @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
      @Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
      @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
      @Result(column = "adx_id", property = "adxId", jdbcType = JdbcType.INTEGER),
      @Result(column = "adx_id", property = "adxName", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.AdxMapper.selectNameByPrimaryKey")),
      @Result(column = "size_id", property = "sizeId", jdbcType = JdbcType.INTEGER),
      @Result(column = "size_id", property = "frameWidth",one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectSizeWidthById")),
      @Result(column = "size_id", property = "frameHeight", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectSizeHeightById")),
      @Result(column = "duration", property = "duration", jdbcType = JdbcType.INTEGER),
      @Result(column = "max_volume", property = "maxVolume", jdbcType = JdbcType.INTEGER),
      @Result(column = "need_image", property = "needImage", jdbcType = JdbcType.VARCHAR),
      @Result(column = "image_max_volume", property = "imageMaxVolume", jdbcType = JdbcType.INTEGER),
      @Result(column = "id", property = "videoFormats", many = @Many(select = "com.pxene.odin.cloud.repository.mapper.basic.VideoPosMapper.selectVideoPosFormatByid"))
  })
  List<VideoPosModel> selectVideoPos();

  @Select({
      "select video_format_id from tb_video_pos_format where video_pos_id=#{video_pos_id}"
  })
  @ResultType(List.class)
  List<Integer> selectVideoPosFormatByid(@Param(value = "video_pos_id") Integer id);

}