package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.CampaignTargetingModel;

public interface CampaignTargetingMapper {
	
    @Insert({
        "insert into tb_campaign_targeting (campaign_id, type, ",
        "is_include, value, ",
        "create_user, update_user)",
        "values (#{campaignId,jdbcType=INTEGER}, #{type,jdbcType=INTEGER}, ",
        "#{isInclude,jdbcType=VARCHAR}, #{value,jdbcType=VARCHAR}, ",
        "#{createUser,jdbcType=INTEGER}, #{updateUser,jdbcType=INTEGER})"
    })
    int insert(CampaignTargetingModel record);
    
    @Select({
        "select",
        "campaign_id, type, is_include, value, create_user, create_time, update_user, ",
        "update_time ",
        "from tb_campaign_targeting",
        "where campaign_id = #{campaignId,jdbcType=INTEGER} ",
        "and type= #{type,jdbcType=VARCHAR} "
    })
    List<CampaignTargetingModel> selectBycampaignIdAndType(@Param("campaignId")Integer campaignId,@Param("type")String type);
    
    @Delete({
        "delete from tb_campaign_targeting",
        "where campaign_id = #{campaignId,jdbcType=INTEGER}",
        "and type = #{type,jdbcType=VARCHAR}"
    })
	void deleteByCampaignIdAndType(@Param("campaignId")Integer campaignId,@Param("type")String type);
}