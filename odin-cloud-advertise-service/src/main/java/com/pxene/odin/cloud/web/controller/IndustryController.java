package com.pxene.odin.cloud.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.service.IndustryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class IndustryController {
	
	@Autowired
	private IndustryService industryService;
	
	@GetMapping(value="/industrys", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listIndustrys() {
		
		List<Map<String, Object>> industrys = industryService.listIndustrys();

		PaginationResponse result = new PaginationResponse(industrys, null);

		return ResponseEntity.ok(result);
	}
	
}
