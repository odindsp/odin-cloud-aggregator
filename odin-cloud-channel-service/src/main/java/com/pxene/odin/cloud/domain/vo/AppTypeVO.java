package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTypeVO {
	/**
	 * APP类型ID
	 */
	private Integer id;
	/**
	 * 一级分类名称
	 */
	private String name;
}
