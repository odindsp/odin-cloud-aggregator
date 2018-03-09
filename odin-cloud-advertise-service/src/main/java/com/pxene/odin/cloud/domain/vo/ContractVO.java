package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractVO {
	/**
	 * 合同ID
	 */
	private Integer id;
	/**
	 * 合同名称
	 */
	private String name;
	/**
	 * 广告平台id
	 */
	private Integer adxId;
	/**
	 * 出价
	 */
	private Integer bid;
	/**
	 * 出价类型
	 */
	private String bidType;
}
