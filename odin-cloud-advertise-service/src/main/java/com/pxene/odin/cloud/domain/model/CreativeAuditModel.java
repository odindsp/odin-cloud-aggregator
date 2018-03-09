package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeAuditModel {

    private Integer creativeId;

    private Integer adxId;

    private String auditValue;

    private String status;

    private String message;

    private String response;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

}
