package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTypeAdxModel {
	
	private Integer appTypeId;
	
	private Integer adxId;
	
	private String oneLevelCode;
	
	private String twoLevelCode;
	
	private String threeLevelCode;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
