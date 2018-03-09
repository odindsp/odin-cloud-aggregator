package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModel {
    private Integer id;

    private String name;

    private String code;

    private Integer advertiserId;

    private Integer industryId;

    private Long capital;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}