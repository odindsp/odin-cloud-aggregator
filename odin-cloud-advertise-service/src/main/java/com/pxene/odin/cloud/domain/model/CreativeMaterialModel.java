package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeMaterialModel {

    private Integer creativeId;

    private Integer materialId;

    private String materialType;

    private Integer orderNo;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}
