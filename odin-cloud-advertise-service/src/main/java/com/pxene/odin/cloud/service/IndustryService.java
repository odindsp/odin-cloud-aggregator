package com.pxene.odin.cloud.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.repository.mapper.basic.IndustryMapper;

@Service
@Transactional
public class IndustryService extends BaseService {
	
	@Autowired
	private IndustryMapper industryMapper;

	public List<Map<String, Object>> listIndustrys() {

		List<Map<String, Object>> industrys = industryMapper.findAllIndustrys();
		return industrys;
	}

}
