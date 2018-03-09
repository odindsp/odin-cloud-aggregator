package com.pxene.odin.cloud.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshiyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class VideoPosVO {

    private Integer id;

    private String name;

    private String code;

    private Integer adxId;

    private String adxName;

    private Integer sizeId;

    private Integer frameWidth;

    private Integer frameHeight;

    private Integer duration;

    private Integer maxVolume;

    private String needImage;

    private Integer imageMaxVolume;

    private List<Integer> videoFormats;
}
