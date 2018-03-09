package com.pxene.odin.cloud.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@NoArgsConstructor
@AllArgsConstructor
public class PackageModel {

	private Integer id;
	
	private String name;
	
	private Integer campaignId;
	
	private String impressionUrl1;
	
	private String impressionUrl2;
	
	private String clickUrl;
	
	private String landpageUrl;
	
	private String isLandpageCode;
	
	private String deeplinkUrl;
		
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
