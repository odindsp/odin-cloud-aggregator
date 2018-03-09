package com.pxene.odin.cloud.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshiyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class InfoflowTmplVO {

  /**
   * 信息流模板ID
   */
  private Integer id;
  /**
   * 信息流模板名称
   */
  private String name;
  /**
   * 标题最大长度
   */
  private Integer titleMaxLen;
  /**
   * 描述填写要求
   */
  private String descriptionRequire;
  /**
   * 描述最大长度
   */
  private Integer descriptionMaxLen;
  /**
   * 行为按钮填写要求
   */
  private String ctaDescRequire;
  /**
   * 行为按钮最大长度
   */
  private Integer ctaDescMaxLen;
  /**
   * 是否需要评分
   */
  private String needGoodsStar;
  /**
   * 是否需要原件
   */
  private String needOriginalPrice;
  /**
   * 是否需要折后价
   */
  private String needDiscountPrice;
  /**
   * 是否需要销量
   */
  private String needSalesVolume;
}
