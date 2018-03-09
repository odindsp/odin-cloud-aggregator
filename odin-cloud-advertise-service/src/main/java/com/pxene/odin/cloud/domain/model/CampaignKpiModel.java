package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignKpiModel {
	
	private Integer campaignId;
	
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