package com.pxene.odin.cloud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.domain.model.RegionModel;
import com.pxene.odin.cloud.domain.vo.RegionVO;
import com.pxene.odin.cloud.domain.vo.RegionVO.City;
import com.pxene.odin.cloud.repository.mapper.basic.RegionMapper;

@Service
@Transactional
public class RegionService extends BaseService{
	
	@Autowired
	RegionMapper regionMapper;
	
	/**
	 * 按省市结构返回
	 * @return
	 */
	public List<RegionVO> listRegions() {
		//查询地域信息
		List<RegionModel> regions = regionMapper.selectRegions();
				
		// 将model数据放到VO中
		List<RegionVO> regionsVO = new ArrayList<RegionVO>();
		for (RegionModel region : regions) {
			RegionVO regionVO = modelMapper.map(region, RegionVO.class);
			regionsVO.add(regionVO);
		}
		// 构造省市结构：区分是省市
		List<RegionVO> provinces = new ArrayList<RegionVO>();
		List<RegionVO> citys = new ArrayList<RegionVO>();
		for (RegionVO region : regionsVO) {
			if (!"000000".equals(region.getId())) {
				// “000000”是未知，以“0000”结尾的是省
				if (region.getId().endsWith("0000")) {
					provinces.add(region);
				} else {
					// 出了未知和省份就是城市
					citys.add(region);
				}
			}
		}
		// 构造省市结构：将市添加到对应的省下
		for (RegionVO province : provinces) {
			List<RegionVO> cityList = new ArrayList<RegionVO>();
			for (RegionVO city : citys) {
				// 省与省对应的市id的特点是前两位相同，将城市按省份分组
				String pId = province.getId().substring(0, 4) + "00";
				String cId = city.getId().substring(0, 2) + "0000";
				if (pId.equals(cId)) {
					cityList.add(city);
				}
			}
			// 将按省份分好组的城市复制到VO的城市信息数组中
			City[] cityPro = new City[cityList.size()];
			for (int i = 0; i < cityList.size(); i ++) {
				cityPro[i] = modelMapper.map(cityList.get(i), City.class);
			}
			// 将城市放到对应的省下
			if (cityPro.length > 0) {
				province.setCitys(cityPro);
			}
		}
		return provinces;
	}
	
	/**
	 * 无结构返回
	 * @return
	 */
	public List<RegionVO> selectRegions() {
		//查询地域信息
		List<RegionModel> regions = regionMapper.selectRegions();
						
		// 将model数据放到VO中
		List<RegionVO> regionsVO = new ArrayList<RegionVO>();
		for (RegionModel region : regions) {
			RegionVO regionVO = modelMapper.map(region, RegionVO.class);
			regionsVO.add(regionVO);
		}
		
		return regionsVO;
	}
}
