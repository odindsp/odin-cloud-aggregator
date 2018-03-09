package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeVO {

	/**
	 * 尺寸ID
	 */
	private String id;

	/**
	 * 宽
	 */
	private Integer width;

	/**
	 * 高
	 */
	private Integer height;

	/**
	 * 展现形式
	 */
	private String adtypeId;
}
