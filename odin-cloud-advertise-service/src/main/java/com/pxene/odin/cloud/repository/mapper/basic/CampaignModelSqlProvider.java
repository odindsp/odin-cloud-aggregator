package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.pxene.odin.cloud.domain.model.CampaignModel;

public class CampaignModelSqlProvider {

	public String findAllCampaigns(Map<String, Object> record) {
		SQL sql = new SQL();
		sql.SELECT("t1.id,t1.name,t1.status,t1.audit_status,t1.enable,t1.project_id,"
				+ "t1.total_budget,t1.total_impression,t1.total_click,"
				+ "t1.bid_type,t1.bid,t1.frequency_type,t1.object_type,t1.cycle_type,"
				+ "t1.frequency_amount,t1.start_date,t1.end_date,"
				+ "t1.population_type,t1.population_ratio,t1.scene_path,t1.scene_radius,"
				+ "t1.scene_name,t1.create_user,t1.create_time,t1.update_user,t1.update_time")
				.FROM("tb_campaign t1").LEFT_OUTER_JOIN("tb_project t2 on t1.project_id=t2.id")
				.LEFT_OUTER_JOIN("tb_advertiser t3 on t2.advertiser_id=t3.id");

		 if (record.get("projectId") != null) {
			sql.WHERE("t1.project_id = #{projectId,jdbcType=INTEGER}");
		 }

		 if (record.get("adStartDate") != null && record.get("adEndDate") != null) {
			sql.WHERE("(t1.start_date BETWEEN #{adStartDate,jdbcType=TIMESTAMP} AND #{adEndDate,jdbcType=TIMESTAMP} "
					+ "OR t1.end_date BETWEEN #{adStartDate,jdbcType=TIMESTAMP} AND #{adEndDate,jdbcType=TIMESTAMP} "
					+ "OR(#{adStartDate,jdbcType=TIMESTAMP} BETWEEN t1.start_date AND t1.end_date "
					+ "AND #{adEndDate,jdbcType=TIMESTAMP} BETWEEN t1.start_date AND t1.end_date))");
		 }

		 if (record.get("id") != null) {
		    sql.WHERE("t1.id = #{id,jdbcType=INTEGER}");
		 }
		
		 if (record.get("name") != null) {
		 sql.WHERE("t1.name like '%"+record.get("name")+"%'");
		 }
		 
		 if(record.get("status")!=null){
			 sql.WHERE("t1.status = #{status,jdbcType=VARCHAR}");
		 }

		 if(record.get("auditStatus")!=null){
			 sql.WHERE("t1.audit_status = #{auditStatus,jdbcType=VARCHAR}");
		 }
		 
		 if (record.get("advertiserName") != null) {
			 sql.WHERE("t3.name like '%"+record.get("advertiserName")+"%'");
		 }
		 
		 if(record.get("sortKey")==null){
			 sql.ORDER_BY("t1.end_date DESC");
		 }
		 
		 if(record.get("sortKey")!=null){
			 sql.ORDER_BY("t1."+record.get("sortKey")+" "+record.get("sortType"));
		 }
		 
		return sql.toString();
	}
	
    public String updateByIdSelective(CampaignModel record) {
        SQL sql = new SQL();
        sql.UPDATE("tb_campaign");
        
        if (record.getName() != null) {
            sql.SET("name = #{name,jdbcType=VARCHAR}");
        }
        
        if (record.getStatus() != null) {
            sql.SET("status = #{status,jdbcType=VARCHAR}");
        }
        
        if (record.getAuditStatus() != null) {
            sql.SET("audit_status = #{auditStatus,jdbcType=VARCHAR}");
        }
        
        if (record.getEnable() != null) {
            sql.SET("enable = #{enable,jdbcType=VARCHAR}");
        }
        
        if (record.getProjectId() != null) {
            sql.SET("project_id = #{projectId,jdbcType=INTEGER}");
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
        
        if (record.getBidType() != null) {
            sql.SET("bid_type = #{bidType,jdbcType=VARCHAR}");
        }
        
        if (record.getBid() != null) {
            sql.SET("bid = #{bid,jdbcType=INTEGER}");
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
        
        if(record.getPopulationType() != null){
        	sql.SET("population_type = #{populationType,jdbcType=VARCHAR}");
        }
        
        if(record.getPopulationRatio() != null){
        	sql.SET("population_ratio = #{populationRatio,jdbcType=REAL}");
        }
        
        if(record.getScenePath() != null){
        	sql.SET("scene_path = #{scenePath,jdbcType=VARCHAR}");
        }
        
        if(record.getSceneRadius() != null){
        	sql.SET("scene_radius = #{sceneRadius,jdbcType=VARCHAR}");
        }
        
        if(record.getSceneName() != null){
        	sql.SET("scene_name = #{sceneName,jdbcType=VARCHAR}");
        }
        
        if (record.getCreateUser() != null) {
            sql.SET("create_user = #{createUser,jdbcType=INTEGER}");
        }
        
        if (record.getUpdateUser() != null) {
            sql.SET("update_user = #{updateUser,jdbcType=INTEGER}");
        }
        
        sql.WHERE("id = #{id,jdbcType=INTEGER}");
        
        return sql.toString();
    }
    
    public String selectByNotId(Map<String,Object> record) {
        SQL sql = new SQL();
        sql.SELECT("t1.id,t1.name,t1.status,t1.audit_status,t1.enable,t1.project_id,"
				+ "t1.total_budget,t1.total_impression,t1.total_click,"
				+ "t1.bid_type,t1.bid,t1.frequency_type,t1.object_type,t1.cycle_type,"
				+ "t1.frequency_amount,t1.start_date,t1.end_date,"
				+ "t1.population_type,t1.population_ratio,t1.scene_path,t1.scene_radius,"
				+ "t1.scene_name,t1.create_user,t1.create_time,t1.update_user,t1.update_time")
        .FROM("tb_campaign t1")
        .LEFT_OUTER_JOIN("tb_project t2 on t1.project_id = t2.id")
        .WHERE("t1.id != #{id,jdbcType=INTEGER}");
        
        if (record.get("name") != null) {
			sql.WHERE("t1.name = #{name,jdbcType=VARCHAR}");
		}
        
        if (record.get("advertiserId") != null) {
			sql.WHERE("t2.advertiser_id = #{advertiserId,jdbcType=INTEGER}");
		}
        
        return sql.toString();
    }
}