package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.InfoflowTmplModel;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
@Mapper
public interface InfoflowTmplMapper {

  @Select({
      "select",
      "id, name, title_max_len, description_require, description_max_len, cta_desc_require, cta_desc_max_len, ",
      "need_goods_star, need_original_price, need_discount_price, need_sales_volume ",
      "from tb_infoflow_tmpl"
  })
  List<InfoflowTmplModel> selectList();

}