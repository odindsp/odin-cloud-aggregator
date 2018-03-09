package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.PolicyCreativeModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface PolicyCreativeMapper {

    @Select("select id,policy_id as policyId,creative_id as creativeId,status,enable,bid,create_user as createUser,"
        + "create_time as createTime,update_user as updateUser,update_time as updateTime from tb_policy_creative"
        + " where policy_id = #{policyId}")
    List<PolicyCreativeModel> seleceByPolicyId(@Param("policyId") Integer policyId);

    @Select("select id,policy_id as policyId,creative_id as creativeId,status,enable,bid,"
        + "create_user as createUser,create_time as createTime,update_user as updateUser,"
        + "update_time as updateTime from tb_policy_creative where id = #{id}")
    PolicyCreativeModel selectByPrimaryKey(@Param("id") Integer id);

    @Select("select id,policy_id from tb_policy_creative where creative_id = #{creative_id}")
    @ResultType(List.class)
    List<Map<String, Integer>> selectByCreativeId(@Param("creative_id") Integer id);

    @Update("update tb_policy_creative set enable=#{enableStr} where id=#{id}")
    Integer updateStatusById(@Param("enableStr") String enableStr, @Param("id") Integer id);

    @SelectProvider(method = "updateByIdSelective", type = PolicyCreativeProvider.class)
    void updateByIdSelective(PolicyCreativeModel record);

    @Options(useGeneratedKeys=true)
    @Insert("insert into tb_policy_creative (id, policy_id, creative_id, `status`, `enable`, bid,"
            + " create_user, create_time, update_user, update_time)"
            + "values (#{id}, #{policyId}, #{creativeId}, #{status}, #{enable}, #{bid}, "
            + "#{createUser}, #{createTime}, #{updateUser}, #{updateTime})")
    void insert(PolicyCreativeModel policyCreativeModel);
    
    @Select("select count(creative_id) from tb_policy_creative where policy_id=#{policyId, jdbcType=INTEGER}")
    Integer selectCreativeCountByPolicyId(@Param("policyId") Integer policyId);

    @Select("select * from tb_policy_creative where policy_id = #{policyId} and creative_id = #{creativeId} ")
    List<PolicyCreativeModel> selectByPolicyIdAndCreativeId(@Param("policyId")Integer policyId, @Param("creativeId")Integer creativeId);
}
