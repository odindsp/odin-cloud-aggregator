package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.PolicyModel;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

public class PolicyModelSqlProvider {

    public String updateByIdSelective(PolicyModel record) {
        SQL sql = new SQL();
        sql.UPDATE("tb_policy");
        
        if (record.getName() != null) {
            sql.SET("name = #{name,jdbcType=VARCHAR}");
        }
        
        if (record.getCampaignId() != null) {
            sql.SET("campaign_id = #{campaignId,jdbcType=INTEGER}");
        }
        
//        if (record.getStatus() != null) {
//            sql.SET("status = #{status,jdbcType=VARCHAR}");
//        }
        
        if (record.getEnable() != null) {
            sql.SET("enable = #{enable,jdbcType=VARCHAR}");
        }
        
        if (record.getTotalBudget() != null) {
            sql.SET("total_budget = #{totalBudget,jdbcType=BIGINT}");
        }
        
        if (record.getTotalImpression() != null) {
            sql.SET("total_impression = #{totalImpression,jdbcType=BIGINT}");
        }
        
        if (record.getTotalClick() != null) {
            sql.SET("total_click = #{totalClick,jdbcType=BIGINT}");
        }
        
        if (record.getRealBid() != null) {
            sql.SET("real_bid = #{realBid,jdbcType=INTEGER}");
        }
        
        if (record.getIsUniform() != null) {
            sql.SET("is_uniform = #{isUniform,jdbcType=VARCHAR}");
        }
        
        if (record.getFrequencyType() != null) {
            sql.SET("frequency_type = #{frequencyType,jdbcType=VARCHAR}");
        }
        
        if (record.getObjectType() != null) {
            sql.SET("object_type = #{objectType,jdbcType=VARCHAR}");
        }
        
        if (record.getCycleType() != null) {
            sql.SET("cycle_type = #{cycleType,jdbcType=VARCHAR}");
        }
        
        if (record.getFrequencyAmount() != null) {
            sql.SET("frequency_amount = #{frequencyAmount,jdbcType=INTEGER}");
        }
        
        if (record.getStartDate() != null) {
            sql.SET("start_date = #{startDate,jdbcType=TIMESTAMP}");
        }
        
        if (record.getEndDate() != null) {
            sql.SET("end_date = #{endDate,jdbcType=TIMESTAMP}");
        }
        
        if (record.getPopulationType() != null) {
            sql.SET("population_type = #{populationType,jdbcType=VARCHAR}");
        }
        
        if (record.getPopulationRatio() != null) {
            sql.SET("population_ratio = #{populationRatio,jdbcType=REAL}");
        }
        
        if (record.getScenePath() != null) {
            sql.SET("scene_path = #{scenePath,jdbcType=VARCHAR}");
        }
        
        if (record.getSceneRadius() != null) {
            sql.SET("scene_radius = #{sceneRadius,jdbcType=VARCHAR}");
        }
        
        if (record.getSceneName() != null) {
            sql.SET("scene_name = #{sceneName,jdbcType=VARCHAR}");
        }
        
        if (record.getCreateUser() != null) {
            sql.SET("create_user = #{createUser,jdbcType=INTEGER}");
        }
        
        if (record.getCreateTime() != null) {
            sql.SET("create_time = #{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getUpdateUser() != null) {
            sql.SET("update_user = #{updateUser,jdbcType=INTEGER}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.SET("update_time = #{updateTime,jdbcType=TIMESTAMP}");
        }
        
        sql.WHERE("id = #{id,jdbcType=INTEGER}");
        
        return sql.toString();
    }
    
    public String findAllPolicys(Map<String, Object> record) {
		SQL sql = new SQL();
		sql.SELECT("id,name,campaign_id,status,enable,total_budget,"
				+ "total_impression,total_click,real_bid,is_uniform,"
				+ "frequency_type,object_type,cycle_type,frequency_amount,"
				+ "start_date,end_date,population_type,population_ratio,"
				+ "scene_path,scene_radius,scene_name,create_user,create_time,"
				+ "update_user,update_time")
				.FROM("tb_policy");

		 if (record.get("campaignId") != null) {
			sql.WHERE("campaign_id = #{campaignId,jdbcType=INTEGER}");
		 }

		 if (record.get("adStartDate") != null && record.get("adEndDate") != null) {
			sql.WHERE("(start_date BETWEEN #{adStartDate,jdbcType=TIMESTAMP} AND #{adEndDate,jdbcType=TIMESTAMP} "
				+ "OR end_date BETWEEN #{adStartDate,jdbcType=TIMESTAMP} AND #{adEndDate,jdbcType=TIMESTAMP} "
				+ "OR(#{adStartDate,jdbcType=TIMESTAMP} BETWEEN start_date AND end_date "
				+ "AND #{adEndDate,jdbcType=TIMESTAMP} BETWEEN start_date AND end_date))");
		 }

		 if (record.get("id") != null) {
		    sql.WHERE("id = #{id,jdbcType=INTEGER}");
		 }
		
		 if (record.get("name") != null) {
		 sql.WHERE("name like '%"+record.get("name")+"%'");
		 }
		 
		 if(record.get("status")!=null){
			 sql.WHERE("status = #{status,jdbcType=VARCHAR}");
		 }

		 if(record.get("sortKey")==null){
			 sql.ORDER_BY("end_date DESC");
		 }
		 
		 if(record.get("sortKey")!=null){
			 sql.ORDER_BY(record.get("sortKey")+" "+record.get("sortType"));
		 }
		 
		return sql.toString();
	}
    
    public String selectByNotId(Map<String,Object> record) {
        SQL sql = new SQL();
        sql.SELECT("id,name,campaign_id,status,enable,total_budget,"
				+ "total_impression,total_click,real_bid,is_uniform,"
				+ "frequency_type,object_type,cycle_type,frequency_amount,"
				+ "start_date,end_date,population_type,population_ratio,"
				+ "scene_path,scene_radius,scene_name,create_user,create_time,"
				+ "update_user,update_time")
				.FROM("tb_policy")
        .WHERE("id != #{id,jdbcType=INTEGER}");
        
        if (record.get("name") != null) {
			sql.WHERE("name = #{name,jdbcType=VARCHAR}");
		}
        
        if (record.get("campaignId") != null) {
			sql.WHERE("campaign_id = #{campaignId,jdbcType=INTEGER}");
		}
        
        return sql.toString();
    }
}