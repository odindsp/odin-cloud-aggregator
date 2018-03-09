package com.pxene.odin.cloud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdxModel {
	
	private Integer id;
	
	private String name;
	
	private String logoPath;
	
	private Float exchangeRate;
	
	private String campanyName;
	
	private String address;
	
	private String domain;
	
	private String contacts;
	
	private String email;
	
	private String needAdvertiserAudit;
	
	private String needCreativeAudit;

	private String supportSsl;

	private String enable;
	
	private String iurl;
	
	private String cturl;
	
	private String aurl;
	
	private String nurl;
	
	private String andrImageTmpl;
	
	private String iosImageTmpl;
	
	private String andrVideoTmpl;
	
	private String iosVideoTmpl;
	
	private String andrInfoflowTmpl;
	
	private String iosInfoflowTmpl;

	private String securityKey;

	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
