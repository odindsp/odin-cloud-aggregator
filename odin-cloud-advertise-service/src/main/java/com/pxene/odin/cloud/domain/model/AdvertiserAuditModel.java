package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告主审核实体
 * @author lizhuoling
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiserAuditModel {
	
	private Integer advertiserId;
	
	private Integer adxId;
	
	private Integer auditValue;
	
	private String status;
	
	private String message;
	
	private String response;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
