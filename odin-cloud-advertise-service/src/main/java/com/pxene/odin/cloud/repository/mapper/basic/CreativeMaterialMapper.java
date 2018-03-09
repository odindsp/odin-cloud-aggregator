package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.CreativeMaterialModel;
import com.pxene.odin.cloud.domain.model.ImageModel;
import com.pxene.odin.cloud.domain.model.VideoModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CreativeMaterialMapper {

    @Insert("insert into tb_creative_material (creative_id, material_id, material_type,"
            + "order_no, create_user, create_time,update_user, update_time)"
            + "values (#{creativeId}, #{materialId}, #{materialType}, #{orderNo}, "
            + "#{createUser}, #{createTime}, #{updateUser}, #{updateTime})")
    int insert(CreativeMaterialModel materialModel);


    @Select("SELECT t2.id, t2.path, t2.format_id, t2.size_id AS sizeId, t2.volume, t1.material_type AS type, t1.order_no AS orderNo "
            + " FROM tb_creative_material t1 "
            + " LEFT JOIN tb_image t2 ON t1.material_id = t2.id "
            + " WHERE t1.creative_id = #{creativeId} ")
    List<ImageModel> selectCreativeImgByCreativeId(@Param("creativeId")Integer creativeId);

    @Select("SELECT t2.id, t2.path, t2.format_id, t2.size_id, t2.time_length AS timeLength, t2.volume, "
            + " t1.material_type AS type, t1.order_no AS orderNo "
            + " FROM tb_creative_material t1 "
            + " LEFT JOIN tb_video t2 ON t1.material_id = t2.id "
            + " WHERE t1.creative_id = #{creativeId} and t1.material_type = #{materialType} ")
    VideoModel selectCreativeVideoByCreativeId(@Param("creativeId")Integer creativeId, @Param("materialType")String materialType);

    @Select("select creative_id as creativeId,material_id as materialId,material_type as materialType,"
    		+ "order_no as orderNo,create_user as createUser,create_time as createTime,update_user as "
    		+ "updateUser,update_time as updateTime from tb_creative_material where creative_id = #{creativeId}")
    List<CreativeMaterialModel> selectByCreativeId(@Param("creativeId")Integer creativeId);
    
    @Select("select creative_id as creativeId,material_id as materialId,material_type as materialType,"
    		+ "order_no as orderNo,create_user as createUser,create_time as createTime,update_user as "
    		+ "updateUser,update_time as updateTime from tb_creative_material where creative_id = #{creativeId} "
    		+ "and material_type = #{materialType}")
    List<CreativeMaterialModel> selectByCreativeIdAndType(@Param("creativeId")Integer creativeId, @Param("materialType")String materialType);
}
