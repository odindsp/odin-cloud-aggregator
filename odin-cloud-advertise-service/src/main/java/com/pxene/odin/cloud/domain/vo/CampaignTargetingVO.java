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
public class CampaignTargetingVO {
	/**
	 * 活动ID
	 */
	private Integer campaignId;
	/**
	 * 定向类型
	 */
    private String type;
	/**
	 * 排除或选择
	 */
    private String isInclude;
    /**
     * 选择定向ID
     */
    private String[] value;
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
