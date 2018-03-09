package com.pxene.odin.cloud.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangshiyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class InfoflowVO {

  /**
   * 原生广告位id
   */
  private Integer id;
  /**
   * 广告位名称
   */
  private String name;
  /**
   * 广告位编号
   */
  private String code;
  /**
   * ADX ID
   */
  private Integer adxId;
  /**
   * ADX 名称
   */
  private String adxName;

  /**
   * 信息流模板
   */
  private InfoflowTmpl infoflowTmpl;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(Include.NON_NULL)
  public static class InfoflowTmpl {
    /**
     * 图片模板
     */
    private List<ImageTmpl> imageTmpls;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class ImageTmpl {

      /**
       * 图片模板ID
       */
      private Integer id;
      /**
       * 图片模板类型
       */
      private String type;
      /**
       * 地址
       */
      private String path;
      /**
       * 体积
       */
      private Integer volume;
      /**
       * 尺寸ID
       */
      private Integer sizeId;
      /**
       * 宽
       */
      private Integer width;
      /**
       * 高
       */
      private Integer height;
      /**
       * 顺序
       */
      private Integer orderNo;
      /**
       * 最大体积
       */
      private Integer maxVolume;

    }

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
     * 是否需要原价
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
    /**
     * 图片格式
     */
    private List<Integer> imageFormats;

  }


}
