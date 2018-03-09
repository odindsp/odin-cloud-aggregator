package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTargetingModel {
	private Integer policyId;

    private String value;

    private String type;
	
    private String isInclude;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}