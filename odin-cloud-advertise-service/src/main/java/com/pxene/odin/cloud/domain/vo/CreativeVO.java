package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.pxene.odin.cloud.common.constant.PhrasesConstant.CREATIVE_PACKAGE_ID_NOT_NULL;
import static com.pxene.odin.cloud.common.constant.PhrasesConstant.CREATIVE_TYPE_NOT_NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreativeVO {

    /**
     * 创意id
     */
    private Integer id;

    /**
     * 创意名称
     */
    private String name;

    /**
     * 创意类型
     */
    @NotNull(message = CREATIVE_TYPE_NOT_NULL)
    private String type;

    /**
     * 物料包ID
     */
    @NotNull(message = CREATIVE_PACKAGE_ID_NOT_NULL)
    private Integer packageId;

    /**
     * 物料包名称
     */
    private String packageName;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * CTA描述（行为按钮）
     */
    private String ctaDesc;

    /**
     * 产品评分
     */
    private Integer goodsStar;

    /**
     * 原价，单位：分
     */
    private Integer originalPrice;

    /**
     * 折扣价，单位：分
     */
    private Integer discountPrice;

    /**
     * 销量
     */
    private Integer salesVolume;

    /**
     * 广告位ID
     */
    private Integer posId;

    /**
     * 奥丁审核状态
     */
    private String auditStatus;

    /**
     * 物料包的开关
     */
    private String  packageEnable;

    /**
     * 创建人
     */
    private Integer createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private Integer updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 格式
     */
    private String format;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

    /**
     * 文件大小
     */
    private Integer volume;

    /**
     * 时长
     */
    private Integer timeLength;

    /**
     * 状态
     */
    private String status;

    /**
     * policyEnable
     */
    private String policyEnable;

    /**
     * 出价
     */
    private Integer bid;

    /**
     * 素材id（图片）
     */
    private Integer[] materialIds;
    
    /**
     * 策略创意关联id
     */
    private Integer mapId;

    /**
     * 素材信息（视频、信息流）
     */
    private Material[] materials;
    private CreativeAudit[] creativeAudits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Material {

        /**
         * 创意ID
         */
        private Integer creativeId;

        /**
         * 素材ID
         */
        private Integer id;

        /**
         * 素材类型
         */
        private String type;

        /**
         * 宽
         */
        private Integer width;

        /**
         * 高
         */
        private Integer height;

        /**
         * 时长
         */
        private Integer timeLength;

        /**
         * 文件大小
         */
        private Integer volume;

        /**
         * 格式
         */
        private Integer format;

        /**
         * 素材路径
         */
        private String path;

        /**
         * 素材顺序
         */
        private Integer orderNo;

        /**
         * 创建人
         */
        private Integer createUser;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新人
         */
        private Integer updateUser;

        /**
         * 更新时间
         */
        private Date updateTime;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreativeAudit {

        /**
         * 创意ID
         */
        private Integer creativeId;

        /**
         * 渠道ID
         */
        private Integer adxId;

        /**
         * 渠道名称
         */
        private String adxName;

        /**
         * 审核值
         */
        private String auditValue;

        /**
         * 状态
         */
        private String status;

        /**
         * 信息
         */
        private String message;

        /**
         * 返回结果
         */
        private String response;

        /**
         * 创建人
         */
        private Integer createUser;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新人
         */
        private Integer updateUser;

        /**
         * 更新时间
         */
        private Date updateTime;

    }

}
