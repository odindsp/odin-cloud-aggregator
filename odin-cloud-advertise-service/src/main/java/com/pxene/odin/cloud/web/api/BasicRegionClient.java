package com.pxene.odin.cloud.web.api;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author lizhuoling
 *
 */
@FeignClient(name = "ODIN-CLOUD-BASIC-SERVICE")
public interface BasicRegionClient {
	
	@RequestMapping(value = "/region", method = RequestMethod.GET)
	public String selectRegions();
	
	@RequestMapping(value = "/regions", method = RequestMethod.GET)
	public String listRegions();
}
