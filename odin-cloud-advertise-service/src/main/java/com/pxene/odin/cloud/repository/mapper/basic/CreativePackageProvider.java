package com.pxene.odin.cloud.repository.mapper.basic;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class CreativePackageProvider {

    public String selectCreatives(Map<String, Object> record) {
        SQL sql = new SQL();
        sql.SELECT("t1.id, t1.`name`, t1.type, t1.package_id AS packageId, t1.title, t1.description,"
                + " t1.cta_desc AS ctaDesc, t1.goods_star AS goodsStar, t1.original_price AS originalPrice,"
                + " t1.discount_price AS discountPrice, t1.sales_volume AS salesVolume, t1.pos_id AS posId,"
                + " t1.audit_status AS auditStatus, t1.`enable`")
                .FROM("tb_creative t1");

        if (record.get("campaignId") != null) {
            sql.LEFT_OUTER_JOIN("tb_package t2 on t1.package_id = t2.id").WHERE("t2.campaign_id = #{campaignId,jdbcType=INTEGER}");
        }
        if (record.get("id") != null) {
            sql.WHERE("t1.package_id = #{id,jdbcType=INTEGER}");
        }

        if(record.get("type") != null){
            sql.WHERE("t1.type = #{type,jdbcType=VARCHAR}");
        }

        if(record.get("auditStatus") != null){
            sql.WHERE("t1.audit_status = #{auditStatus,jdbcType=VARCHAR}");
        }

        return sql.toString();
    }

    public String seleceByPolicyIdAndCreativeType(Map<String, Object> record) {
        SQL sql = new SQL();
        sql.SELECT(" t1.id, t1.`name`, t1.type, t1.package_id AS packageId, t1.title, t1.description,"
                + " t1.cta_desc AS ctaDesc, t1.goods_star AS goodsStar, t1.original_price AS originalPrice,"
                + " t1.discount_price AS discountPrice, t1.sales_volume AS salesVolume, t1.pos_id AS posId,"
                + " t1.audit_status AS auditStatus, t1.`enable`, t2.`status`, t2.`enable` AS policyEnable, t2.bid, t2.id AS mapId")
                .FROM("tb_creative t1").LEFT_OUTER_JOIN("tb_policy_creative t2 ON t1.id = t2.creative_id")
                .WHERE("t2.policy_id = #{id,jdbcType=INTEGER}");

        if(record.get("type") != null){
            sql.WHERE("t1.type = #{type,jdbcType=VARCHAR}");
        }

        if(record.get("auditStatus") != null){
            sql.WHERE("t1.audit_status = #{auditStatus,jdbcType=VARCHAR}");
        }

        return sql.toString();
    }

}
