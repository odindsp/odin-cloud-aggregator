package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.PolicyCreativeModel;
import org.apache.ibatis.jdbc.SQL;

public class PolicyCreativeProvider {

    public String updateByIdSelective(PolicyCreativeModel record) {
        SQL sql = new SQL();
        sql.UPDATE("tb_policy_creative");

        if (record.getStatus() != null) {
            sql.SET("status = #{status,jdbcType=VARCHAR}");
        }

        if (record.getEnable() != null) {
            sql.SET("enable = #{enable,jdbcType=VARCHAR}");
        }

        if (record.getBid() != null) {
            sql.SET("bid = #{bid,jdbcType=INTEGER}");
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
}
