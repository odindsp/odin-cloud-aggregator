package com.pxene.odin.cloud.service;

import com.google.common.reflect.TypeToken;
import com.pxene.odin.cloud.common.PhrasesConstant;
import com.pxene.odin.cloud.domain.model.SizeModel;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.repository.mapper.basic.SizeMapper;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.domain.vo.SizeVO;

@Service
@Transactional
public class SizeService extends BaseService {

  @Autowired
  private SizeMapper sizeMapper;

  public List<SizeVO> listSizes() {
    List<SizeModel> sizeModels = sizeMapper.selectSizes();
    if (sizeModels != null) {
      return modelMapper.map(sizeModels, new TypeToken<List<SizeVO>>() {
      }.getType());
    }
    return null;
  }

  public SizeVO getSize(Integer id) {
    SizeModel sizeModel = sizeMapper.selectByPrimaryKey(id);
    if (sizeModel == null) {
      throw new ResourceNotFoundException(PhrasesConstant.IMAGE_SIZE_NOT_FOUND);
    }
    return modelMapper.map(sizeModel, SizeVO.class);
  }

}
