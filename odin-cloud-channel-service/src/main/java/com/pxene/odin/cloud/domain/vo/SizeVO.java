package com.pxene.odin.cloud.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
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
   * 展现形式ID
   */
	private String adtypeId;
}
