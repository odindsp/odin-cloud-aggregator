package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.CampaignKpiModel;

public interface CampaignKpiMapper {
    @Insert({
        "insert into tb_campaign_kpi (campaign_id, day, ",
        "is_lock, period, daily_budget, ",
        "daily_impression, daily_click, ",
        "create_user, update_user)",
        "values (#{campaignId,jdbcType=INTEGER}, #{day,jdbcType=DATE}, ",
        "#{isLock,jdbcType=VARCHAR}, #{period,jdbcType=BIGINT}, #{dailyBudget,jdbcType=BIGINT}, ",
        "#{dailyImpression,jdbcType=BIGINT}, #{dailyClick,jdbcType=BIGINT}, ",
        "#{createUser,jdbcType=INTEGER}, #{updateUser,jdbcType=INTEGER})"
    })
    int insert(CampaignKpiModel record);
    
    @Select({
        "select",
        "campaign_id, day, is_lock, period, daily_budget, daily_impression, daily_click, ",
        "create_user, create_time, update_user, update_time",
        "from tb_campaign_kpi",
        "where campaign_id = #{campaignId,jdbcType=INTEGER}"
    })
    List<CampaignKpiModel> selectByCampaignId(Integer campaignId);
    
	@Delete({
	        "delete from tb_campaign_kpi",
	        "where campaign_id = #{campaignId,jdbcType=INTEGER}"
	    })
	void deleteByCampaignId(Integer campaignId);
	
	@Select("select campaign_id as campaignId, day, is_lock as isLock, period, daily_budget as dailyBudget,"
			+ "daily_impression as dailyImpression, daily_click as dailyClick, create_user as createUser, "
			+ "create_time as createTime, update_user as updateUser, update_time as updateTime from tb_campaign_kpi "
			+ "where campaign_id = #{campaignId,jdbcType=INTEGER} and day = #{day,jdbcType=DATE}")
	CampaignKpiModel selectByPrimaryKey(@Param("campaignId")Integer campaignId, @Param("day")Date day);
}