package com.pxene.odin.cloud.domain.vo;

import static com.pxene.odin.cloud.common.constant.PhrasesConstant.BID_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.BUDGEGET_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.CAMPAIGN_NAME_NOT_NULL;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.CLICK_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.END_DATE_ERROR_CODE;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.IMPRESSION_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.LENGTH_ERROR_CAMPAIGN_NAME;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.PROJECT_ID_NOT_NULL;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.START_DATE_ERROR_CODE;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.TARGET_NOT_NULL;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CampaignVO extends DataVO{
	/**
	 * 活动ID
	 */
	private Integer id;
	/**
	 * 活动名称
	 */
	
	@NotNull(message=CAMPAIGN_NAME_NOT_NULL)
	@Length(max=20, message=LENGTH_ERROR_CAMPAIGN_NAME)
	private String name;
	/**
	 * 活动状态
	 */
	private String status;
	/**
	 * 审核状态
	 */
	private String auditStatus;
	/**
	 * 开关状态
	 */
	private String enable;
	/**
	 * 项目ID
	 */
	@NotNull(message=PROJECT_ID_NOT_NULL)
	private Integer projectId;
	/**
	 * 总成本
	 */
	@Min(value=0,message=BUDGEGET_NOT_MINUS)
	private Long totalBudget;
	/**
	 * 总展现
	 */
	@Min(value=0,message=IMPRESSION_NOT_MINUS)
	private Long totalImpression;
	/**
	 * 总点击
	 */
	@Min(value=0,message=CLICK_NOT_MINUS)
	private Long totalClick;
	/**
	 * 出价方式
	 */
	private String bidType;
	/**
	 * 出价
	 */
	@Min(value=0,message=BID_NOT_MINUS)
	private Integer bid;
	/**
	 * 频次类型
	 */
	private String frequencyType;
	/**
	 * 对象类型
	 */
	private String objectType;
	/**
	 * 周期类型
	 */
	private String cycleType;
	/**
	 * 次数
	 */
	private Integer frequencyAmount;
	/**
	 * 开始时间
	 */
	@NotNull(message=START_DATE_ERROR_CODE)
	private Long startDate;
	/**
	 * 结束时间
	 */
	@NotNull(message=END_DATE_ERROR_CODE)
	private Long endDate;
	/**
	 * 人群包类型
	 */
	private String populationType;
	/**
	 * 溢价比例
	 */
    private Float populationRatio;
    /**
     * 坐标文件路径
     */
    private String scenePath;
    /**
     * 精度范围
     */
    private String sceneRadius;
    /**
     * 上传文件名称
     */
    private String sceneName;
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
	/**
	 * 广告主ID
	 */
	private Integer advertiserId;
	/**
	 * 项目名称
	 */
	private String projectName;
	/**
	 * 投放中策略数
	 */
	private Integer advertisingAmount;
	/**
	 * 总策略数
	 */
	private Integer totalAmount;
	/**
	 * 物料包数量
	 */
	private Integer packageAmount;
	/**
	 * 广告主名称
	 */
	private String advertiserName;
	/**
	 * kpi信息
	 */
	private CampaignKpiVO[] kpi;
	/**
	 * 定向信息
	 */
	@NotNull(message=TARGET_NOT_NULL)
	@Valid
	private CampaignTargetVO targeting; 
}