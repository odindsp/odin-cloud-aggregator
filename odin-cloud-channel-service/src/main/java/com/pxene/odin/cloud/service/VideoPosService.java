package com.pxene.odin.cloud.service;

import static com.pxene.odin.cloud.service.BaseService.modelMapper;

import com.google.common.reflect.TypeToken;
import com.pxene.odin.cloud.common.PhrasesConstant;
import com.pxene.odin.cloud.domain.model.VideoPosModel;
import com.pxene.odin.cloud.domain.vo.VideoPosVO;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.repository.mapper.basic.VideoPosMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhangshiyi
 */
@Service
@Transactional
public class VideoPosService {

  @Autowired
  private VideoPosMapper videoPosMapper;


  public List<VideoPosVO> listVideoPoses() {
    List<VideoPosModel> videoPosModel = videoPosMapper.selectVideoPos();
    if (videoPosModel != null) {
      return modelMapper.map(videoPosModel, new TypeToken<List<VideoPosVO>>() {
      }.getType());
    }
    return null;
  }

  public VideoPosVO getVideoPosById(Integer id) {
    VideoPosModel videoPosModel = videoPosMapper.selectByPrimaryKey(id);
    if (videoPosModel == null) {
      throw new ResourceNotFoundException(PhrasesConstant.VIDEO_POS_NOT_FOUND);
    }
    return modelMapper.map(videoPosModel, VideoPosVO.class);
  }
}
