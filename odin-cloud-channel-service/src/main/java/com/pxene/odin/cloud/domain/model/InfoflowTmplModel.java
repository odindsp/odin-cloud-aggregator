package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoflowTmplModel {

  private Integer id;

  private String name;

  private Integer titleMaxLen;

  private String descriptionRequire;

  private Integer descriptionMaxLen;

  private String ctaDescRequire;

  private Integer ctaDescMaxLen;

  private String needGoodsStar;

  private String needOriginalPrice;

  private String needDiscountPrice;

  private String needSalesVolume;

}