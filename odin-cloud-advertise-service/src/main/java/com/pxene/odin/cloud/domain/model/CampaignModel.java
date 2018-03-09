package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignModel {
    private Integer id;

    private String name;

    private String status;
    
    private String auditStatus;

    private String enable;

    private Integer projectId;

    private Long totalBudget;

    private Long totalImpression;

    private Long totalClick;

    private String bidType;

    private Integer bid;

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