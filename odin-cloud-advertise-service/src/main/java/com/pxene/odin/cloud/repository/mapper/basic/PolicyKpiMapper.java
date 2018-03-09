package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import com.pxene.odin.cloud.domain.model.PolicyKpiModel;

public interface PolicyKpiMapper {

    @Insert({
        "insert into tb_policy_kpi (policy_id, day, ",
        "is_lock, period, daily_budget, ",
        "daily_impression, daily_click, ",
        "create_user, update_user)",
        "values (#{policyId,jdbcType=INTEGER}, #{day,jdbcType=DATE}, ",
        "#{isLock,jdbcType=VARCHAR}, #{period,jdbcType=BIGINT}, #{dailyBudget,jdbcType=BIGINT}, ",
        "#{dailyImpression,jdbcType=BIGINT}, #{dailyClick,jdbcType=BIGINT}, ",
        "#{createUser,jdbcType=INTEGER}, #{updateUser,jdbcType=INTEGER})"
    })
    int insert(PolicyKpiModel record);
    
    @Select({
        "select",
        "policy_id, day, is_lock, period, daily_budget, daily_impression, daily_click, ",
        "create_user, create_time, update_user, update_time",
        "from tb_policy_kpi",
        "where policy_id = #{policyId,jdbcType=INTEGER}"
    })
    List<PolicyKpiModel> selectByPolicyId(Integer policyId);
    
    @Delete({
        "delete from tb_policy_kpi",
        "where policy_id = #{policyId,jdbcType=INTEGER}"
    })
    void deleteByPolicyId(Integer policyId);
    
    @Select("select policy_id as policyId, day, is_lock as isLock, period, daily_budget as dailyBudget, "
    		+ "daily_impression as dailyImpression, daily_click as dailyClick,"
    		+ "create_user, create_time, update_user, update_time from tb_policy_kpi where "
    		+ "policy_id = #{policyId} and day = #{day}")
    PolicyKpiModel selectByPrimaryKey(@Param("policyId")Integer policyId, @Param("day")Date day);
}