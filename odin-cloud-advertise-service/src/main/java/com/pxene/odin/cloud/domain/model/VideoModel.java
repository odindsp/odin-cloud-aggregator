package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoModel {
    private Integer id;

    private String path;

    private Integer formatId;

    private Integer sizeId;

    private Integer height;

    private Integer width;

    private Integer volume;

    private Integer timeLength;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

}