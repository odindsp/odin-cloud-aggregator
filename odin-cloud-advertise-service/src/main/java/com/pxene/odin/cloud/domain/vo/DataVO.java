package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataVO {
	/**
	 * 展现量
	 */
	private Long  impression=0L;
	/**
	 * 点击量
	 */
	private Long click=0L;
	/**
	 * ctr
	 */
	private Double ctr=0.0;
	/**
	 * 成本
	 */
	private Double cost=0.0;
	/**
	 * ecpm
	 */
	private Double ecpm=0.0;
	/**
	 * ecpc
	 */
	private Double ecpc=0.0;
}
