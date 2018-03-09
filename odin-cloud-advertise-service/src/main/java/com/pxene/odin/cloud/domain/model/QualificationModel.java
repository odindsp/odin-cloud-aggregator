package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualificationModel {
	
	private Integer advertiserId;
	
	private String type;
	
	private String path;
	
	private Integer createUser;
	
	private Integer createTime;
	
	private Integer updateUser;
	
	private Integer updateTime;
}
