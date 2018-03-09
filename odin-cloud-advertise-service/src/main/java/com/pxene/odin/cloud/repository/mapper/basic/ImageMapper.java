package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.ImageModel;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

public interface ImageMapper {


    @Delete({"delete from tb_image", "where id = #{id,jdbcType=INTEGER}"})
    int deleteByPrimaryKey(Integer id);

    @Options(useGeneratedKeys = true)
    @Insert({"insert into tb_image (id, path, ", "format_id, size_id, ", "volume, create_user, ", "create_time, update_user, ", "update_time)",
        "values (#{id,jdbcType=INTEGER}, #{path,jdbcType=VARCHAR}, ", "#{formatId,jdbcType=INTEGER}, #{sizeId,jdbcType=INTEGER}, ",
        "#{volume,jdbcType=INTEGER}, #{createUser,jdbcType=INTEGER}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{updateUser,jdbcType=INTEGER}, ", "#{updateTime,jdbcType=TIMESTAMP})"})
    int insert(ImageModel record);


    @Select({"select", "id, path, format_id, size_id, volume, create_user, create_time, update_user, update_time", "from tb_image",
        "where id = #{id,jdbcType=INTEGER}"})
    @Results({@Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
        @Result(column = "path", property = "path", jdbcType = JdbcType.VARCHAR),
        @Result(column = "format_id", property = "formatId", jdbcType = JdbcType.INTEGER),
        @Result(column = "size_id", property = "sizeId", jdbcType = JdbcType.INTEGER),
        @Result(column = "volume", property = "volume", jdbcType = JdbcType.INTEGER),
        @Result(column = "create_user", property = "createUser", jdbcType = JdbcType.INTEGER),
        @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "update_user", property = "updateUser", jdbcType = JdbcType.INTEGER),
        @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.TIMESTAMP)})
    ImageModel selectByPrimaryKey(Integer id);


    @Update({"update tb_image", "set path = #{path,jdbcType=VARCHAR},", "format_id = #{formatId,jdbcType=INTEGER},",
        "size_id = #{sizeId,jdbcType=INTEGER},", "volume = #{volume,jdbcType=INTEGER},", "create_user = #{createUser,jdbcType=INTEGER},",
        "create_time = #{createTime,jdbcType=TIMESTAMP},", "update_user = #{updateUser,jdbcType=INTEGER},",
        "update_time = #{updateTime,jdbcType=TIMESTAMP}", "where id = #{id,jdbcType=INTEGER}"})
    int updateByPrimaryKey(ImageModel record);


    @Select({"<script> SELECT * FROM  tb_image <where> id in ( SELECT DISTINCT  t.material_id FROM tb_creative_material t LEFT JOIN  "
        + "tb_creative t1 ON t.`creative_id`=t1.id  LEFT JOIN tb_package t2 ON t2.`id`=t1.`package_id` LEFT JOIN tb_campaign t3 "
        + "ON t3.id=t2.`campaign_id` LEFT JOIN tb_project t4 ON t4.id=t3.project_id <where>"
        + "<if test=\"advertiserId !=null\"> t4.advertiser_id=#{advertiserId} </if>"
        + "<if test=\"projectId !=null\"> and  t3.project_id=#{projectId} </if>"
        + "<if test=\"campaignId !=null\"> and t2.`campaign_id`=#{campaignId} </if>"
        + "<if test=\"creativeId !=null\"> and t.creative_id =#{creativeId} </if></where>)"
        + " <if test=\"sizeId !=null \"> and size_id=#{sizeId} </if> </where></script>"})
    List<ImageModel> selectBySizeId(@Param(value = "advertiserId")Integer advertiserId, @Param(value = "campaignId") Integer campaignId, @Param(value = "creativeId") Integer creativeId,
        @Param(value = "projectId") Integer projectId, @Param(value = "sizeId") Integer sizeId);
}