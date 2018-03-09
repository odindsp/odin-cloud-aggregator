package com.pxene.odin.cloud.domain.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeModel {
	
	private Integer id;
	
	private Integer width;
	
	private Integer height;
	
	private String adtypeId;
	
	private Integer createUser;
	
	private Date createTime;
	
	private Integer updateUser;
	
	private Date updateTime;
}
