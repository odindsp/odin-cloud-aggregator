package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.pxene.odin.cloud.domain.model.ProjectModel;

public class ProjectModelSqlProvider {

	public String findAllProjects(Map<String, Object> record) {
		SQL sql = new SQL();
		sql.SELECT("t1.id,t1.name,t1.code,t1.advertiser_id,t1.industry_id,"
				+ "t1.capital,t1.create_user,t1.create_time,t1.update_user,t1.update_time")
				.FROM("tb_project t1").LEFT_OUTER_JOIN("tb_advertiser t2 on t1.advertiser_id=t2.id");
		if (record.get("advertiserId") != null) {
			sql.WHERE("t2.id = #{advertiserId,jdbcType=INTEGER}");
		}
		
		if (record.get("advertiserName") != null) {
			sql.WHERE("t2.name like '%"+record.get("advertiserName")+"%'");
		}

		if (record.get("id") != null) {
			sql.WHERE("t1.id = #{id,jdbcType=INTEGER}");
		}

		 if (record.get("name") != null) {
		 sql.WHERE("t1.name like '%"+record.get("name")+"%'");
		 }
		
		 if (record.get("code") != null) {
		 sql.WHERE("t1.code like '%"+record.get("code")+"%'");
		 }
		 
		 if(record.get("sortKey")==null){
			 sql.ORDER_BY("t1.create_time DESC");
		 }
		 
		 if(record.get("sortKey")!=null){
			 sql.ORDER_BY("t1."+record.get("sortKey")+" "+record.get("sortType"));
		 }

		return sql.toString();
	}

	public String updateByIdSelective(ProjectModel record) {
		SQL sql = new SQL();
		sql.UPDATE("tb_project");

		if (record.getName() != null) {
			sql.SET("name = #{name,jdbcType=VARCHAR}");
		}

		if (record.getCode() != null) {
			sql.SET("code = #{code,jdbcType=VARCHAR}");
		}

		if (record.getAdvertiserId() != null) {
			sql.SET("advertiser_id = #{advertiserId,jdbcType=INTEGER}");
		}

		if (record.getIndustryId() != null) {
			sql.SET("industry_id = #{industryId,jdbcType=INTEGER}");
		}

		if (record.getCapital() != null) {
			sql.SET("capital = #{capital,jdbcType=BIGINT}");
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
        sql.SELECT("id,name,code,advertiser_id,industry_id,capital,"
				+ "create_user,create_time,update_user,"
				+ "update_time")
        .FROM("tb_project ").WHERE("id != #{id,jdbcType=INTEGER}");
        
        if (record.get("name") != null) {
			sql.WHERE("name = #{name,jdbcType=VARCHAR}");
		}
        
        if (record.get("code") != null) {
			sql.WHERE("code = #{code,jdbcType=VARCHAR}");
		}
        
		if (record.get("advertiserId") != null){
			sql.WHERE("advertiser_id = #{advertiserId,jdbcType=INTEGER}");
		}
        
        return sql.toString();
    }
}