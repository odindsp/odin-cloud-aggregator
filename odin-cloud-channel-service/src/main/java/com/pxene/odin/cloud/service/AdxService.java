package com.pxene.odin.cloud.service;

import com.pxene.odin.cloud.common.ConfKeyConstant;
import com.pxene.odin.cloud.common.PhrasesConstant;
import com.pxene.odin.cloud.domain.model.AdxModel;
import com.pxene.odin.cloud.domain.model.ContractModel;
import com.pxene.odin.cloud.domain.vo.AdxVO;
import com.pxene.odin.cloud.domain.vo.ContractVO;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.repository.mapper.basic.AdxMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ContractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AdxService extends BaseService{

	@Autowired
	AdxMapper adxMapper;

	@Autowired
	ContractMapper contractMapper;
	
	private String prefix;
	
	@Autowired
	public AdxService(Environment env) {
		prefix = env.getProperty(ConfKeyConstant.FILESERVER_REMOTE_URL_PREFIX);
	}

	/**
	 * 批量查询ADX
	 * @return
	 */
	public List<AdxVO> listAdxs() {
		// 查询ADX
		List<AdxModel> adxsModel = adxMapper.selectAdxs();
		// 将ADX信息复制到对应的VO中
		List<AdxVO> adxs = new ArrayList<AdxVO>();
		if (adxsModel != null && !adxsModel.isEmpty()) {
			for (AdxModel adxModel : adxsModel) {
				AdxVO adx = modelMapper.map(adxModel, AdxVO.class);
				String logoPath = adxModel.getLogoPath();
				if (logoPath != null && !logoPath.isEmpty()) {
					adx.setLogoPath(prefix + logoPath);
				}				
				// 将复制后单个ADX实体信息放到list中
				adxs.add(adx);
			}
		}
		return adxs;
	}

	/**
	 * 批量查询定价合同
	 * @return
	 */
	public List<ContractVO> listAdxContracts() {
		// 查询定价合同
		List<ContractModel> contractsModel = contractMapper.selectContracts();
		// 将ADX信息复制到对应的VO中
		List<ContractVO> contracts = new ArrayList<ContractVO>();
		if (contractsModel != null && !contractsModel.isEmpty()) {
			for (ContractModel contractModel : contractsModel) {
				ContractVO contract = modelMapper.map(contractModel, ContractVO.class);
				// 获取广告平台logo路径
				Integer adxId = contractModel.getAdxId();
				String logoPath = adxMapper.selectLogoPathByPrimaryKey(adxId);
				if (logoPath != null && !logoPath.isEmpty()) {
					contract.setLogoPath(prefix + logoPath);
				}				
				// 将复制后单个定价合同实体信息放到list中
				contracts.add(contract);
			}
		}
		return contracts;

	}

	public AdxVO getAdx(Integer id){
		AdxModel adxModel = adxMapper.selectByPrimaryKey(id);
		if (adxModel == null) {
			throw new ResourceNotFoundException(PhrasesConstant.ADX_NOT_FOUND);
		}
		return modelMapper.map(adxModel, AdxVO.class);
	}

	/**
	 * 根据id查询定价信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ContractVO selectContractById(Integer id) throws Exception {
		 ContractModel  contract = contractMapper.selectContractById(id);
		 if (contract == null) {
			 throw new ResourceNotFoundException(PhrasesConstant.CONTRACT_NOT_FOUND);
		 }
		 return modelMapper.map(contract, ContractVO.class);
	}
	
	/**
	 * 根据合同的开始时间查询定价合同信息
	 * @param startDate 合同的开始时间
	 * @return
	 * @throws Exception
	 */
	public List<ContractVO> selectContractByTodayStart(Date startDate) throws Exception {
		List<ContractModel> contracts = contractMapper.selectContractByStartDate(startDate);
		List<ContractVO> contractsVO = new ArrayList<ContractVO>();
		if (contracts != null && !contracts.isEmpty()) {
			for (ContractModel contract : contracts) {
				ContractVO contractVO = modelMapper.map(contract, ContractVO.class);
				contractsVO.add(contractVO);
			}
		}
		return contractsVO;		
	}
	
	/**
	 * 根据合同的结束时间查询定价合同信息
	 * @param endDate 合同的结束时间
	 * @return
	 * @throws Exception
	 */
	public List<ContractVO> selectContractByEndStart(Date endDate) throws Exception {
		List<ContractModel> contracts = contractMapper.selectContractByEndDate(endDate);
		List<ContractVO> contractsVO = new ArrayList<ContractVO>();
		if (contracts != null && !contracts.isEmpty()) {
			for (ContractModel contract : contracts) {
				ContractVO contractVO = modelMapper.map(contract, ContractVO.class);
				contractsVO.add(contractVO);
			}
		}
		return contractsVO;			
	}
}
