package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppVO {
	/**
	 * 	APP（媒体） ID
	 */
	private String 	id;
	/**
	 * APP名称
	 */
	private String name;
	/**
	 * 所属ADX ID
	 */
	private Integer adxId;
	/**
	 * 	所属ADX名称
	 */
	private String adxName;
	/**
	 * 下载地址
	 */
	private String downloadUrl;
	/**
	 * 所属系统
	 */
	private Integer os;
	/**
	 * 包名
	 */
	private String packageName;
	/**
	 * 一级分类编码
	 */
	private String oneLevelCode;
	/**
	 * 一级分类名称
	 */
	private String oneLevelName;	
	/**
	 * 	二级分类编码
	 */
	private String 	twoLevelCode;
	/**
	 * 二级分类名称
	 */
	private String twoLevelName;
	/**
	 * app的搜索关键字
	 */
	private String[] names;
	/**
	 * 搜索类型
	 */
	private String searchType;
}
