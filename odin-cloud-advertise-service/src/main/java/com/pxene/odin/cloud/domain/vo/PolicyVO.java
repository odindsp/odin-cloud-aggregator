package com.pxene.odin.cloud.domain.vo;

import static com.pxene.odin.cloud.common.constant.PhrasesConstant.BUDGEGET_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.CLICK_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.END_DATE_ERROR_CODE;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.IMPRESSION_NOT_MINUS;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.LENGTH_ERROR_NAME;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.NAME_NOT_NULL;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PolicyVO extends DataVO{
	/**
	 * 策略ID
	 */
    private Integer id;
    /**
     * 策略名称
     */
    @NotNull(message=NAME_NOT_NULL)
    @Length(max = 20, message = LENGTH_ERROR_NAME)
    private String name;
    /**
     * 活动ID
     */
    private Integer campaignId;
    /**
     * 策略状态
     */
    private String status;
    /**
     * 开关状态
     */
    private String enable;
    /**
     * 总成本
     */
    @Min(value=0,message=BUDGEGET_NOT_MINUS)
    private Long totalBudget;
    /**
     * 总展现
     */
    @Min(value=0, message=IMPRESSION_NOT_MINUS)
    private Long totalImpression;
    /**
     * 总点击
     */
    @Min(value=0, message=CLICK_NOT_MINUS)
    private Long totalClick;
    /**
     * 真实出价
     */
    private Integer realBid;
    /**
     * 是否匀速投放
     */
    private String isUniform;
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
     * 创意数
     */
    private Integer creativeAmount;
    /**
     * 评分
     */
    private Integer score;
    /**
     * kpi信息
     */
    private PolicyKpiVO[] kpi;
    /**
     * 定向信息
     */
    @NotNull(message=TARGET_NOT_NULL)
	@Valid
    private PolicyTargetVO targeting; 
}