package com.pxene.odin.cloud.domain.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.*;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO extends DataVO{
	/**
	 * 项目ID
	 */
	private Integer id;
	/**
	 * 项目名称
	 */
	@NotNull(message=NAME_NOT_NULL)
	@Length(max=20, message=LENGTH_ERROR_NAME)
	private String name;
	/**
	 * 项目编号
	 */
	@NotNull(message=CODE_NOT_NULL)
	@Length(min=14,max=14,message=LENGTH_ERROR_CODE)
	private String code;
	/**
	 * 广告主ID
	 */
	@NotNull(message=ADVERTISER_ID_NOT_NULL)
	private Integer advertiserId;
	/**
	 * 行业ID
	 */
	@NotNull(message=INDUSTRY_ID_NOT_NULL)
	private Integer industryId;
	/**
	 * 总预算
	 */
	@NotNull(message=CAPITAL_NOT_FOUND)
	private Long capital;
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
	 * 广告主名称
	 */
	private String advertiserName;
	/**
	 * 行业名称
	 */
	private String industryName;
	/**
	 * 投放中活动数
	 */
	private Integer advertisingAmount;
	/**
	 * 总活动数
	 */
	private Integer totalAmount;
}