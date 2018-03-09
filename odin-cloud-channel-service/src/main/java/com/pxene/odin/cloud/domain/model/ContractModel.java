package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractModel {
	
	private String id;
	
	private String name;
	
	private Integer adxId;
	
	private Date startDate;
	
	private Date endDate;
	
	private Integer bid;
	
	private String bidType;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
