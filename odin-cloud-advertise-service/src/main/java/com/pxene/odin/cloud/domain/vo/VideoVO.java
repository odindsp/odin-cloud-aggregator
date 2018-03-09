package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangshiyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoVO {
    private Integer id;
    private String path;
    private Integer formatId;
    private Integer width;
    private Integer height;
    private Integer sizeId;
    private Integer volume;
    private Integer time_length;
}
