package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.CreativeAuditModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface CreativeAuditMapper {

    @Select("SELECT creative_id AS creativeId, adx_id AS adxId, audit_value AS auditValue, `status`, message, response"
            + " FROM tb_creative_audit"
            + " WHERE creative_id = #{creativeId}")
    List<CreativeAuditModel> selectCreativeAuditByCreativeId(@Param("creativeId")Integer creativeId);


}
