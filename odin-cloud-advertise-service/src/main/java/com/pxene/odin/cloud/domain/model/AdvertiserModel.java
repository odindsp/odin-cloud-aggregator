package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiserModel {
	private Integer id;
	
	private String name;
	
	private String companyName;
	
	private String isProtected;
	
	private String contacts;
	
	private String contactNum;
	
	private String email;
	
	private String qq;
	
	private Integer industryId;
	
	private String brand;
	
	private String logoPath;
	
	private String accountLicencePath;
	
	private String businessLicencePath;
	
	private String organizationCodePath;
	
	private String icpPath;
	
	private String licenceNo;
	
	private String licenceDeadline;
	
	private String organizationCode;
	
	private String telephone;
	
	private String address;
	
	private String zip;
	
	private String websiteUrl;
	
	private String websiteName;
	
	private String saleman;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
