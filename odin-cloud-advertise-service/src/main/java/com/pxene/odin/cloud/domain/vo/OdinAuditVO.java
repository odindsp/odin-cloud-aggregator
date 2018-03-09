package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshiyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OdinAuditVO {

    private String auditStatus;
    private Integer[] ids;
}
