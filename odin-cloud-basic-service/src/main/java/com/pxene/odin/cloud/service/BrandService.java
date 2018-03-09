package com.pxene.odin.cloud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.domain.model.BrandModel;
import com.pxene.odin.cloud.domain.vo.BrandVO;
import com.pxene.odin.cloud.repository.mapper.basic.BrandMapper;

@Service
@Transactional
public class BrandService extends BaseService{

	@Autowired
	BrandMapper brandMapper;
	
	/**
	 * 批量查询手机品牌
	 * @return
	 * @throws Exception
	 */
	public List<BrandVO> listBrands() throws Exception {
		// 查询手机品牌
		List<BrandModel> brandModels = brandMapper.selectBrands();
		// 手机品牌信息
		List<BrandVO> brands = new ArrayList<BrandVO>();
		if (brandModels != null && !brandModels.isEmpty()) {
			for (BrandModel brandModel : brandModels) {
				// 将model的手机品牌信息复制到对应的vo中
				BrandVO brand = modelMapper.map(brandModel, BrandVO.class);
				brands.add(brand);
			}
		}
		return brands;		
	}
}
