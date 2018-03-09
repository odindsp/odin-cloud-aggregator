package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeModel {

    private Integer id;

    private String name;

    private String type;

    private Integer packageId;

    private String title;

    private String description;

    private String ctaDesc;

    private Integer goodsStar;

    private Integer originalPrice;

    private Integer discountPrice;

    private Integer salesVolume;

    private Integer posId;

    private String auditStatus;

    private String enable;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

    private String status;

    private String policyEnable;

    private Integer bid;
    
    private Integer mapId;

}
