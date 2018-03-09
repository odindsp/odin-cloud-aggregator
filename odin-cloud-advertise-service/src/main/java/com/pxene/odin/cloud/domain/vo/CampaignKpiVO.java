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
public class CampaignKpiVO {
	/**
	 * 活动ID
	 */
	private Integer campaignId;
	/**
	 * 日期
	 */
	private Date day;
	/**
	 * 是否锁定
	 */
    private String isLock;
    /**
     * 时段
     */
    private Long period;
    /**
     * 日成本
     */
    private Long dailyBudget;
    /**
     * 日展现
     */
    private Long dailyImpression;
    /**
     * 日点击
     */
    private Long dailyClick;
    /**
     * 创建人
     */
    private Integer createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private Integer updateUser;
    /**
     * 更新时间
     */
    private Date updateTime;
}