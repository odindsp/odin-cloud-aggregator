package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.PolicyTargetingModel;

public interface PolicyTargetingMapper {

    @Insert({
        "insert into tb_policy_targeting (policy_id, value, ",
        "type, is_include, ",
        "create_user, update_user)",
        "values (#{policyId,jdbcType=INTEGER}, #{value,jdbcType=VARCHAR}, ",
        "#{type,jdbcType=VARCHAR}, #{isInclude,jdbcType=VARCHAR}, ",
        "#{createUser,jdbcType=INTEGER}, #{updateUser,jdbcType=INTEGER})"
    })
    int insert(PolicyTargetingModel record);
    
    @Select({
        "select",
        "policy_id, type, is_include, value, create_user, create_time, update_user, ",
        "update_time ",
        "from tb_policy_targeting",
        "where policy_id = #{policyId,jdbcType=INTEGER} ",
        "and type= #{type,jdbcType=VARCHAR} "
    })
    List<PolicyTargetingModel> selectByPolicyIdAndType(@Param("policyId")Integer policyId,@Param("type")String type);
    
    @Delete({
        "delete from tb_policy_targeting",
        "where policy_id = #{policyId,jdbcType=INTEGER}",
        "and type = #{type,jdbcType=VARCHAR}"
    })
	void deleteByPolicyIdAndType(@Param("policyId")Integer policyId,@Param("type")String type);
    
    @Select("select policy_id, type, is_include, value, create_user, create_time, update_user,"
    		+ "update_time from tb_policy_targeting where policy_id = #{policyId,jdbcType=INTEGER}")
    List<PolicyTargetingModel> selectByPolicyId(@Param("policyId")Integer policyId);
}