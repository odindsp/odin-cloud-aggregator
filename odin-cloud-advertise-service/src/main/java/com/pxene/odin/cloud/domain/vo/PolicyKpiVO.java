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
public class PolicyKpiVO {
	
    private Integer policyId;
	
    private Date day;
	
    private String isLock;

    private Long period;

    private Long dailyBudget;

    private Long dailyImpression;

    private Long dailyClick;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}