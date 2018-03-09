package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyModel {
    private Integer id;

    private String name;

    private Integer campaignId;

    private String status;

    private String enable;

    private Long totalBudget;

    private Long totalImpression;

    private Long totalClick;

    private Integer realBid;

    private String isUniform;

    private String frequencyType;

    private String objectType;

    private String cycleType;

    private Integer frequencyAmount;

    private Date startDate;

    private Date endDate;

    private String populationType;

    private Float populationRatio;

    private String scenePath;

    private String sceneRadius;
    
    private String sceneName;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}