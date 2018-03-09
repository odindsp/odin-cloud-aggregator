package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.pxene.odin.cloud.domain.model.CampaignModel;
import org.apache.ibatis.annotations.Update;

public interface CampaignMapper {
	
	@Select({
	     "select",
	     "t1.id, t1.name, t1.status, t1.enable, t1.audit_status, t1.project_id, t1.total_budget, t1.total_impression, t1.total_click, ",
	     "t1.bid_type, t1.bid, t1.frequency_type, t1.object_type, t1.cycle_type,t1.frequency_amount, t1.start_date, ",
	     "t1.end_date, t1.population_type, t1.population_ratio, t1.scene_path, t1.scene_radius, t1.scene_name,t1.create_user, ",
	     "t1.create_time, t1.update_user, t1.update_time ",
	     "from tb_campaign t1 left join tb_project t2 on t1.project_id = t2.id ",
	     "where t1.name = #{name,jdbcType=VARCHAR} and t2.advertiser_id = #{advertiserId,jdbcType=INTEGER}"
	    })
	    List<CampaignModel> selectByNameAndAdvertiserId(@Param("name") String name, @Param("advertiserId") Integer advertiserId);
	
    @Select({
        "select",
        "id, name, status, enable, audit_status, project_id, total_budget, total_impression, total_click, ",
        "bid_type, bid, frequency_type, object_type, cycle_type, frequency_amount, start_date, ",
        "end_date, population_type, population_ratio, scene_path, scene_radius, scene_name,create_user, ",
        "create_time, update_user, update_time ",
        "from tb_campaign",
        "where id = #{id,jdbcType=INTEGER}"
    })
    CampaignModel selectByPrimaryKey(Integer id);
    
    @Options(useGeneratedKeys=true)
    @Insert({
        "insert into tb_campaign (id, name, ",
        "status, enable, audit_status, ",
        "project_id, total_budget, ",
        "total_impression, total_click, ",
        "bid_type, bid, frequency_type, ",
        "object_type, cycle_type, ",
        "frequency_amount, start_date, ",
        "end_date, population_type, ",
        "population_ratio, scene_path, ",
        "scene_radius, scene_name, ",
        "create_user, update_user)",
        "values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=VARCHAR}, #{enable,jdbcType=VARCHAR}, #{auditStatus,jdbcType=VARCHAR}, ",
        "#{projectId,jdbcType=INTEGER}, #{totalBudget,jdbcType=BIGINT}, ",
        "#{totalImpression,jdbcType=BIGINT}, #{totalClick,jdbcType=BIGINT}, ",
        "#{bidType,jdbcType=VARCHAR}, #{bid,jdbcType=INTEGER}, #{frequencyType,jdbcType=VARCHAR}, ",
        "#{objectType,jdbcType=VARCHAR}, #{cycleType,jdbcType=VARCHAR}, ",
        "#{frequencyAmount,jdbcType=INTEGER}, #{startDate,jdbcType=TIMESTAMP}, ",
        "#{endDate,jdbcType=TIMESTAMP}, #{populationType,jdbcType=VARCHAR}, ",
        "#{populationRatio,jdbcType=REAL}, #{scenePath,jdbcType=VARCHAR}, ",
        "#{sceneRadius,jdbcType=VARCHAR},  #{sceneName,jdbcType=VARCHAR},",
        "#{createUser,jdbcType=INTEGER}, #{updateUser,jdbcType=INTEGER})"
    })
    int insert(CampaignModel record);
    
    @SelectProvider(type=CampaignModelSqlProvider.class , method="findAllCampaigns")
	List<CampaignModel> findAllCampaigns(Map<String, Object> map);
    
    @SelectProvider(type=CampaignModelSqlProvider.class, method="updateByIdSelective")
	void updateByIdSelective(CampaignModel campaignModel);
    
    @SelectProvider(type=CampaignModelSqlProvider.class, method="selectByNotId")
    List<CampaignModel> selectByNotId(Map<String,Object> map);

    @Update({"update tb_campaign set audit_status=#{auditStatus} where id=#{id}"})
    Integer updateauditStatus(@Param("auditStatus")String auditStatus,@Param("id")Integer id);
    
    @Select({
        "select * from tb_campaign where start_date = #{startDate,jdbcType=TIMESTAMP}"
    })
    List<CampaignModel> findCampaignsByStartDate(@Param("startDate") Date startDate);
    
    @Select({
        "select * from tb_campaign where end_date = #{endDate,jdbcType=TIMESTAMP}"
    })
    List<CampaignModel> findCampaignsByEndDate(@Param("endDate") Date endDate);
    
    @Select({
        "select count(*) from tb_campaign where project_id = #{projectId,jdbcType=INTEGER}"
    })
    Integer selectCampaignCountByProjectId(@Param("projectId") Integer projectId);
    
    @Update({
        "update tb_campaign",
        "set name = #{name,jdbcType=VARCHAR},",
          "audit_status = #{auditStatus,jdbcType=VARCHAR},",
          "enable = #{enable,jdbcType=VARCHAR},",
          "project_id = #{projectId,jdbcType=INTEGER},",
          "total_budget = #{totalBudget,jdbcType=BIGINT},",
          "total_impression = #{totalImpression,jdbcType=BIGINT},",
          "total_click = #{totalClick,jdbcType=BIGINT},",
          "bid_type = #{bidType,jdbcType=VARCHAR},",
          "bid = #{bid,jdbcType=INTEGER},",
          "frequency_type = #{frequencyType,jdbcType=VARCHAR},",
          "object_type = #{objectType,jdbcType=VARCHAR},",
          "cycle_type = #{cycleType,jdbcType=VARCHAR},",
          "frequency_amount = #{frequencyAmount,jdbcType=INTEGER},",
          "start_date = #{startDate,jdbcType=TIMESTAMP},",
          "end_date = #{endDate,jdbcType=TIMESTAMP},",
          "population_type = #{populationType,jdbcType=VARCHAR},",
          "population_ratio = #{populationRatio,jdbcType=REAL},",
          "scene_path = #{scenePath,jdbcType=VARCHAR},",
          "scene_radius = #{sceneRadius,jdbcType=VARCHAR},",
          "scene_name = #{sceneName,jdbcType=VARCHAR},",
          "create_user = #{createUser,jdbcType=INTEGER},",
          "update_user = #{updateUser,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(CampaignModel record);
}