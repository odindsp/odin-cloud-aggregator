package com.pxene.odin.cloud.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoPosVo {

    private Integer id;

    private String name;

    private String code;

    private Integer adxId;

    private Integer frameWidth;

    private Integer frameHeight;

    private Integer duration;

    private Integer maxVolume;

    private String needimage;

    private Integer imageMaxVolume;

    private List<Integer> videoPosFormat;

}
