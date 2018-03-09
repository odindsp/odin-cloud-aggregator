package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pxene.odin.cloud.domain.model.ContractModel;

@Mapper
public interface ContractMapper {

	@Select("select id,name,adx_id as adxId,bid from tb_contract")
	List<ContractModel> selectContracts();
	
	@Select("select id,name,adx_id as adxId,bid,bid_type as bidType from tb_contract where id = #{id}")
	ContractModel selectContractById(@Param("id")Integer id);
	
	@Select("select id,name,adx_id as adxId,bid,bid_type as bidType from tb_contract where start_date = #{startDate}")
	List<ContractModel> selectContractByStartDate(@Param("startDate") Date startDate);
	
	@Select("select id,name,adx_id as adxId,bid,bid_type as bidType from tb_contract where end_date = #{endDate}")
	List<ContractModel> selectContractByEndDate(@Param("endDate") Date endDate);
}
