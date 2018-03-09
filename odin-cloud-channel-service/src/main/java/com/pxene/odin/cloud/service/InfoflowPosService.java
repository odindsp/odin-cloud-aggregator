package com.pxene.odin.cloud.service;

import com.google.common.reflect.TypeToken;
import com.pxene.odin.cloud.common.PhrasesConstant;
import com.pxene.odin.cloud.domain.model.InfoflowPosCustomModel;
import com.pxene.odin.cloud.domain.model.InfoflowTmplModel;
import com.pxene.odin.cloud.domain.vo.InfoflowTmplVO;
import com.pxene.odin.cloud.domain.vo.InfoflowVO;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper;
import com.pxene.odin.cloud.repository.mapper.basic.InfoflowTmplMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhangshiyi
 */
@Service
@Transactional
public class InfoflowPosService extends BaseService {

  @Autowired
  private InfoflowTmplMapper infoflowTmplMapper;
  @Autowired
  private InfoflowPosMapper infoflowPosMapper;

  public List<InfoflowTmplVO> listInfoflowTmpl() {
    List<InfoflowTmplModel> InfoflowTmplModels = infoflowTmplMapper.selectList();
    if (InfoflowTmplModels != null) {
      return modelMapper.map(InfoflowTmplModels, new TypeToken<List<InfoflowTmplModel>>() {
      }.getType());
    }
    return null;
  }

  public InfoflowVO getInfoflow(Integer id) {
    InfoflowPosCustomModel infoflowPosModel = infoflowPosMapper.findOneToOne(id);
    if (infoflowPosModel == null) {
      throw new ResourceNotFoundException(PhrasesConstant.INFOFLOW_POS_NOT_FOUND);
    }
    return modelMapper.map(infoflowPosModel, InfoflowVO.class);
  }

  public List<InfoflowVO> listInfoflowPos() {
    List<InfoflowPosCustomModel> infoflowPos = infoflowPosMapper.listInfoflowPos();
    if (infoflowPos != null) {
      return modelMapper.map(infoflowPos, new TypeToken<List<InfoflowPosCustomModel>>() {
      }.getType());
    }
    return null;
  }
}
