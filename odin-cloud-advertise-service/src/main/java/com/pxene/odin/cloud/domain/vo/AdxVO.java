package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdxVO {
	/**
	 * 渠道ID
	 */
	private Integer id;

	/**
	 * 渠道名称
	 */
	private String name;
}
