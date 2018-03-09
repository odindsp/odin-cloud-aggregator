package com.pxene.odin.cloud.domain.vo;

import static com.pxene.odin.cloud.common.constant.PhrasesConstant.ADX_NOT_NULL;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PolicyTargetVO {
	/**
	 * 渠道定向
	 */
	@NotNull(message=ADX_NOT_NULL)
	private String[] adx;
	/**
	 * 定价合同定向
	 */
	private String[] contract;
	/**
	 * 媒体定向
	 */
	private PolicyTargetingVO app;
	/**
	 * 媒体类型定向
	 */
	private PolicyTargetingVO appType;
	/**
	 * 品牌定向
	 */
	private String[] brand;
	/**
	 * 运营商定向
	 */
	private String[] carrier;
	/**
	 * 设备定向
	 */
	private String[] device;
	/**
	 * 网络定向
	 */
	private String[] network;
	/**
	 * 系统定向
	 */
	private String[] os;
	/**
	 * 人群包定向
	 */
	private String[] population;
	/**
	 * 地域定向
	 */
	private String[] region;
}
