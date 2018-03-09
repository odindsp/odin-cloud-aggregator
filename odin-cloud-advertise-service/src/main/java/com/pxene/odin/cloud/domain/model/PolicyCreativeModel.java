package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCreativeModel {
	
	private Integer id;
	
	private Integer policyId;
	
	private Integer creativeId;
	
	private String status;
	
	private String enable;
	
	private Integer bid;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
