package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandVO {
	/**
	 * 	品牌ID
	 */
	private Integer id;
	/**
	 * 中文名称
	 */
	private String cnName;
	/**
	 * 英文名称
	 */
	private String enName;
	/**
	 * 等级
	 */
	private String grade;
}
