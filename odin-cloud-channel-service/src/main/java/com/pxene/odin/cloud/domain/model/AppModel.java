package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppModel {
	
	private String id;
	
	private Integer adxId;
	
	private String name;
	
	private String pkgName;
	
	private Integer typeId;
	
	private Integer osType;
	
	private String downloadUrl;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
