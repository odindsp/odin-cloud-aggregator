package com.pxene.odin.cloud.domain.model;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoPosModel {

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

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

    private List<Integer> videoFormats;


}