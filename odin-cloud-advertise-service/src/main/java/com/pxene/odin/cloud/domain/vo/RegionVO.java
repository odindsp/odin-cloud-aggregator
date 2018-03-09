package com.pxene.odin.cloud.domain.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionVO {
	/**
	 * 	地域ID
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
	 * 纬度
	 */
	private BigDecimal latitude;
	/**
	 * 城市数组
	 */
	private City[] citys;
	@Data
	public static class City {
		/**
		 * 城市ID
		 */
		private String id;
		/**
		 * 城市名称
		 */
		private String name;
		/**
		 * 经度
		 */
		private BigDecimal longitude;
		/**
		 * 纬度
		 */
		private BigDecimal latitude;
	}
}
