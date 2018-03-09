package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.pxene.odin.cloud.domain.model.PolicyModel;

public interface PolicyMapper {
    @Options(useGeneratedKeys = true)
    @Insert({
        "insert into tb_policy (id, name, ",
        "campaign_id, status, ",
        "enable, total_budget, ",
        "total_impression, total_click, ",
        "real_bid, is_uniform, ",
        "frequency_type, object_type, ",
        "cycle_type, frequency_amount, ",
        "start_date, end_date, ",
        "population_type, population_ratio, ",
        "scene_path, scene_radius, scene_name, ",
        "create_user, update_user)",
        "values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "#{campaignId,jdbcType=INTEGER}, #{status,jdbcType=VARCHAR}, ",
        "#{enable,jdbcType=VARCHAR}, #{totalBudget,jdbcType=BIGINT}, ",
        "#{totalImpression,jdbcType=BIGINT}, #{totalClick,jdbcType=BIGINT}, ",
        "#{realBid,jdbcType=INTEGER}, #{isUniform,jdbcType=VARCHAR}, ",
        "#{frequencyType,jdbcType=VARCHAR}, #{objectType,jdbcType=VARCHAR}, ",
        "#{cycleType,jdbcType=VARCHAR}, #{frequencyAmount,jdbcType=INTEGER}, ",
        "#{startDate,jdbcType=TIMESTAMP}, #{endDate,jdbcType=TIMESTAMP}, ",
        "#{populationType,jdbcType=VARCHAR}, #{populationRatio,jdbcType=REAL}, ",
        "#{scenePath,jdbcType=VARCHAR}, #{sceneRadius,jdbcType=VARCHAR}, ",
        "#{sceneName,jdbcType=VARCHAR}, #{createUser,jdbcType=INTEGER}, ",
        "#{updateUser,jdbcType=INTEGER}) "
    })
    int insert(PolicyModel record);
	
    @Select({
        "select",
        "id, name, campaign_id, status, enable, total_budget, total_impression, total_click, ",
        "real_bid, is_uniform, frequency_type, object_type, cycle_type, frequency_amount, ",
        "start_date, end_date, population_type, population_ratio, scene_path, scene_radius, ",
        "scene_name, create_user, create_time, update_user, update_time",
        "from tb_policy",
        "where id = #{id,jdbcType=INTEGER}"
    })
    PolicyModel selectByPrimaryKey(Integer id);
    
    @SelectProvider(type=PolicyModelSqlProvider.class, method="updateByIdSelective")
	void updateByIdSelective(PolicyModel policyModel);
    
    @SelectProvider(type=PolicyModelSqlProvider.class, method="findAllPolicys")
	List<PolicyModel> findAllPolicys(Map<String, Object> map);
    

    @Select({
        "select",
        "id, name, campaign_id, status, enable, total_budget, total_impression, total_click, ",
        "real_bid, is_uniform, frequency_type, object_type, cycle_type, frequency_amount, ",
        "start_date, end_date, population_type, population_ratio, scene_path, scene_radius, ",
        "scene_name, create_user, create_time, update_user, update_time",
        "from tb_policy",
        "where name = #{name,jdbcType=VARCHAR} and campaign_id = #{campaignId,jdbcType=INTEGER}"
    })
	List<PolicyModel> selectByNameAndCampaignId(@Param("name") String name,@Param("campaignId") Integer campaignId);
    
    @SelectProvider(type=PolicyModelSqlProvider.class, method="selectByNotId")
	List<PolicyModel> selectByNotId(Map<String, Object> map);
    
    @Select({
        "select * from tb_policy where start_date = #{startDate,jdbcType=TIMESTAMP}"
    })
    List<PolicyModel> findPolicysByStartDate(@Param("startDate") Date startDate);
    
    @Select({
        "select * from tb_policy where end_date = #{endDate,jdbcType=TIMESTAMP}"
    })
    List<PolicyModel> findPolicysByEndDate(@Param("endDate") Date endDate);
    
    @Select({
        "select * from tb_policy where start_date <= #{currentDate,jdbcType=TIMESTAMP} and end_date >= #{currentDate,jdbcType=TIMESTAMP}"
    })
    List<PolicyModel> findPolicysByCurrentDate(@Param("currentDate")Date currentDate);
    
    @Select({
        "select count(*) from tb_policy where campaign_id = #{campaignId,jdbcType=INTEGER}"
    })
	Integer selectPolicyCountByCampaignId(@Param("campaignId") Integer campaignId);
}