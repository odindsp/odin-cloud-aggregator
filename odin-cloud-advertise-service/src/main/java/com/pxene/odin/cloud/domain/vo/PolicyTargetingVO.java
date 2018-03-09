package com.pxene.odin.cloud.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PolicyTargetingVO {
	
    private Integer policyId;

    private String type;
	
    private String isInclude;

    private String[] value;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}
