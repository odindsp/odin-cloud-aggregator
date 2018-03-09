package com.pxene.odin.cloud.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionModel {
	/**
	 * 地域id
	 */
	private String id;
	/**
	 * 地域名称
	 */
	private String name;
	/**
	 * 经度
	 */
	private BigDecimal longitude;
	/**
	 * 维度
	 */
	private BigDecimal latitude;
}
