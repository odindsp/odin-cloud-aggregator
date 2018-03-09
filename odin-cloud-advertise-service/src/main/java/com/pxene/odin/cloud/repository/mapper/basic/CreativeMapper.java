package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.CreativeModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CreativeMapper {

    @Options(useGeneratedKeys=true)
    @Insert("insert into tb_creative (`id`, `name`, `type`, `package_id`, `title`, `description`, `cta_desc`, "
            + " `goods_star`, `original_price`, `discount_price`, `sales_volume`, `pos_id`, "
            + " `audit_status`, `enable`, `create_user`, `create_time`, `update_user`, `update_time`)"
            + " values (#{id}, #{name}, #{type}, #{packageId}, #{title}, #{description}, #{ctaDesc}, "
            + " #{goodsStar}, #{originalPrice}, #{discountPrice}, #{salesVolume}, #{posId}, "
            + " #{auditStatus}, #{enable}, #{createUser}, #{createTime}, #{updateUser}, #{updateTime})")
    int insert(CreativeModel creative);

    @SelectProvider(method = "selectCreatives", type = CreativePackageProvider.class)
    List<CreativeModel> selectCreatives(Map<String, Object> record);


    @Select("SELECT id, `name`, type, package_id AS packageId, title, description, cta_desc AS ctaDesc,"
            + " goods_star AS goodsStar, original_price AS originalPrice, discount_price AS discountPrice,"
            + " sales_volume AS salesVolume, pos_id AS posId, audit_status AS auditStatus, `enable`"
            + " FROM tb_creative WHERE id = #{id}")
    CreativeModel selectByPrimaryKey(@Param("id")Integer id);

    @SelectProvider(method = "seleceByPolicyIdAndCreativeType", type = CreativePackageProvider.class)
    List<CreativeModel> seleceByPolicyIdAndCreativeType(Map<String, Object> record);

    @Update("update tb_creative set enable=#{enableStr} where id=#{id}")
    Integer updateEnableById(@org.apache.ibatis.annotations.Param(value = "enableStr") String enableStr,
        @org.apache.ibatis.annotations.Param(value = "id") Integer id);
    
    @Select("SELECT id, `name`, type, package_id AS packageId, title, description, cta_desc AS ctaDesc,"
            + " goods_star AS goodsStar, original_price AS originalPrice, discount_price AS discountPrice,"
            + " sales_volume AS salesVolume, pos_id AS posId, audit_status AS auditStatus, `enable`"
            + " FROM tb_creative WHERE package_id = #{packageId}")
    List<CreativeModel> selectByPackageId(@Param("packageId")Integer packageId);

    @Update("<script> update tb_creative set audit_status=#{auditStatus} where id in "
        +"<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>" +
        "#{item}" +
        "</foreach> </script>")
    Integer auditCreative(@org.apache.ibatis.annotations.Param("auditStatus")String auditStatus, @org.apache.ibatis.annotations.Param("ids")Integer[] ids);

    @Update("update tb_creative set name=#{name} where id=#{id}")
    Integer updateCreativeName(@org.apache.ibatis.annotations.Param("id") Integer id, @org.apache.ibatis.annotations.Param("name")String name);
}
