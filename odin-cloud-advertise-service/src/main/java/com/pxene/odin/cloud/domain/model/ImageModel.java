package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {
    private Integer id;

    private String path;

    private Integer formatId;

    private Integer sizeId;

    private Integer volume;

    private String type;

    private Integer orderNo;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

}