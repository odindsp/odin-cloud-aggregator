package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdxVO {
	/**
	 * 渠道id（编号）
	 */
	private Integer id;

	/**
	 * 渠道名称
	 */
	private String name;

	/**
	 * LOGO图片路径
	 */
	private String  logoPath;

	/**
	 * 人民币汇率
	 */
	private Float exchangeRate;

	/**
	 *
	 */
	private String campanyName;

	/**
	 *
	 */
	private String address;

	/**
	 *
	 */
	private String domain;

	/**
	 *
	 */
	private String contacts;

	/**
	 *
	 */
	private String email;

	/**
	 * 是否需要广告主审核
	 */
	private String needAdvertiserAudit;

	/**
	 * 	是否需要创意审核
	 */
	private String needCreativeAudit;

	/**
	 * 是否支持安全连接
	 */
	private String supportSsl;

	/**
	 * 开关
	 */
	private String enable;

	/**
	 * 展现监测
	 */
	private String iurl;

	/**
	 * 点击监测
	 */
	private String cturl;

	/**
	 * 激活监测
	 */
	private String aurl;

	/**
	 * 结算地址
	 */
	private String nurl;

	/**
	 * 安卓图片模板
	 */
	private String andrImageTmpl;

	/**
	 * ios图片模板
	 */
	private String iosImageTmpl;

	/**
	 * 安卓视频模板
	 */
	private String andrVideoTmpl;

	/**
	 * ios视频模板
	 */
	private String iosVideoTmpl;

	/**
	 * 安卓信息流模板
	 */
	private String andrInfoflowTmpl;

	/**
	 * ios信息流模板
	 */
	private String iosInfoflowTmpl;

	/**
	 * 私密KEY
	 */
	private String securityKey;

	/**
	 *
	 */
	private Integer createUser;

	/**
	 *
	 */
	private Date createTime;

	/**
	 *
	 */
	private Integer updateUser;

	/**
	 *
	 */
	private Date updateTime;

	/**
	 * 支持尺寸
	 */
	private String[] sizes;

}
