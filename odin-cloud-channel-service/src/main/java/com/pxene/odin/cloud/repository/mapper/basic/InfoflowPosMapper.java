package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.InfoflowPosCustomModel;

import java.util.List;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface InfoflowPosMapper {

    @Select(" select id,name,code,adx_id,infoflow_tmpl_id from tb_infoflow_pos ")
    @Results(
            id = "tb_infoflow_poss",
            value = {
                    @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
                    @Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "adx_id", property = "adxId", jdbcType = JdbcType.INTEGER),
                    @Result(column = "adx_id", property = "adxName", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.AdxMapper.selectNameByPrimaryKey")),
                    @Result(column = "infoflow_tmpl_id", property = "infoflowTmpl", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectInfoflowTmplById")),
            })
    List<InfoflowPosCustomModel> listInfoflowPos();

    @Select(" select id,name,code,adx_id,infoflow_tmpl_id from tb_infoflow_pos where id=#{id}")
    @Results(
            id = "tb_infoflow_pos",
            value = {
                    @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
                    @Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "code", property = "code", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "adx_id", property = "adxId", jdbcType = JdbcType.INTEGER),
                    @Result(column = "adx_id", property = "adxName", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.AdxMapper.selectNameByPrimaryKey")),
                    @Result(column = "infoflow_tmpl_id", property = "infoflowTmpl", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectInfoflowTmplById")),
            })
    InfoflowPosCustomModel findOneToOne(@Param("id") int id);


    @Select({
            "select",
            "id, name, title_max_len, description_require, description_max_len, cta_desc_require, cta_desc_max_len, ",
            "need_goods_star, need_original_price, need_discount_price, need_sales_volume, ",
            "create_user, create_time, update_user, update_time",
            "from tb_infoflow_tmpl",
            "where id = #{id,jdbcType=INTEGER}"
    })
    @Results(
            id = "tb_infoflow_tmpl",
            value = {
                    @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
                    @Result(column = "name", property = "name", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "title_max_len", property = "titleMaxLen", jdbcType = JdbcType.INTEGER),
                    @Result(column = "description_require", property = "descriptionRequire", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "description_max_len", property = "descriptionMaxLen", jdbcType = JdbcType.INTEGER),
                    @Result(column = "cta_desc_require", property = "ctaDescRequire", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "cta_desc_max_len", property = "ctaDescMaxLen", jdbcType = JdbcType.INTEGER),
                    @Result(column = "need_goods_star", property = "needGoodsStar", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "need_original_price", property = "needOriginalPrice", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "need_discount_price", property = "needDiscountPrice", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "need_sales_volume", property = "needSalesVolume", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "id", property = "imageFormats", many = @Many(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectInfoflowTmplFormatById")),
                    @Result(column = "id", property = "imageTmpls", many = @Many(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectImageTmplById"))
            })
    InfoflowPosCustomModel.InfoflowTmpl selectInfoflowTmplById(@Param("id") Integer id);

    @Select({
            "select",
            "id, infoflow_tmpl_id, type, size_id, order_no, max_volume ",
            "from tb_image_tmpl",
            "where infoflow_tmpl_id = #{infoflow_tmpl_id,jdbcType=INTEGER}"
    })
    @Results(
            id = "tb_image_tmpl",
            value = {
                    @Result(column = "id", property = "id", jdbcType = JdbcType.INTEGER, id = true),
                    @Result(column = "type", property = "type", jdbcType = JdbcType.VARCHAR),
                    @Result(column = "size_id", property = "sizeId", jdbcType = JdbcType.INTEGER),
                    @Result(column = "size_id", property = "width",one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectSizeWidthById")),
                    @Result(column = "size_id", property = "height", one = @One(select = "com.pxene.odin.cloud.repository.mapper.basic.InfoflowPosMapper.selectSizeHeightById")),
                    @Result(column = "order_no", property = "orderNo", jdbcType = JdbcType.INTEGER),
                    @Result(column = "max_volume", property = "maxVolume", jdbcType = JdbcType.INTEGER),
            })
    InfoflowPosCustomModel.InfoflowTmpl.ImageTmpl selectImageTmplById(
            @Param("infoflow_tmpl_id") Integer id);

    @Select({
            "select height from tb_size where id=#{id}"
    })
    Integer selectSizeHeightById(@Param(value = "id") Integer id);

    @Select({
            "select width from tb_size where id=#{id}"
    })
    Integer selectSizeWidthById(@Param(value = "id") Integer id);

    @Select({
            "select image_format_id from tb_infoflow_tmpl_format where infoflow_tmpl_id=#{infoflow_tmpl_id}"
    })
    @ResultType(List.class)
    List<Integer> selectInfoflowTmplFormatById(@Param(value = "infoflow_tmpl_id") Integer id);


}