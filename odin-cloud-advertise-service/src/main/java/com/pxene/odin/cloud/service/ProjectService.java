package com.pxene.odin.cloud.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.common.constant.StatusConstant;
import com.pxene.odin.cloud.common.enumeration.CampaignState;
import com.pxene.odin.cloud.common.util.CamelCaseUtil;
import com.pxene.odin.cloud.domain.model.AdvertiserModel;
import com.pxene.odin.cloud.domain.model.CampaignModel;
import com.pxene.odin.cloud.domain.model.ProjectModel;
import com.pxene.odin.cloud.domain.vo.ProjectVO;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.repository.mapper.basic.AdvertiserMapper;
import com.pxene.odin.cloud.repository.mapper.basic.CampaignMapper;
import com.pxene.odin.cloud.repository.mapper.basic.IndustryMapper;
import com.pxene.odin.cloud.repository.mapper.basic.ProjectMapper;

@Service
@Transactional
public class ProjectService extends BaseService {

	@Autowired
	ProjectMapper projectMapper;
	@Autowired
	AdvertiserMapper advertiserMapper;
	@Autowired
	CampaignMapper campaignMapper;
	@Autowired
	IndustryMapper industryMapper;
	@Autowired
	CampaignService campaignService;

	public void updateProject(ProjectModel project) {
		if (project.getId() != null) {
			projectMapper.updateByIdSelective(project);
		}
	}

	public ProjectModel findById(Integer id) {
		return projectMapper.selectProjectById(id);
	}

	public ProjectModel saveProject(ProjectVO project) {
		ProjectModel projectModel = modelMapper.map(project, ProjectModel.class);
		projectMapper.insert(projectModel);
		return projectModel;
	}

	ProjectModel findByNameAndAdvertiserId(String name,Integer advertiserId) {

		List<ProjectModel> list = projectMapper.selectProjectByNameAndAdvertiserId(name,advertiserId);

		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	ProjectModel findByCode(String code) {
		List<ProjectModel> list = projectMapper.selectProjectByCode(code);

		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public boolean isProjectCodeExist(ProjectVO project) {
		return findByCode(project.getCode()) != null;
	}

	public boolean isProjectNameExist(ProjectVO project) {
		return findByNameAndAdvertiserId(project.getName(),project.getAdvertiserId()) != null;
	}

	public ProjectVO getProjectVO(ProjectModel project) {
		ProjectVO projectVO = modelMapper.map(project, ProjectVO.class);
		getParam4Bean(projectVO);
		return projectVO;
	}

	private void getParam4Bean(ProjectVO bean) {
		Integer advertiserId = bean.getAdvertiserId();
		AdvertiserModel advertiser = advertiserMapper.selectByPrimaryKey(advertiserId);
		if (advertiser != null) {
			bean.setAdvertiserName(advertiser.getName());
		}
		Integer projectId = bean.getId();
		//查询项目下投放中活动数
		Integer advertisingAmount=0;
		Map<String,Object> map = new HashMap<>();
		map.put("projectId", projectId);
		List<CampaignModel> campaigns = campaignMapper.findAllCampaigns(map);
		for (CampaignModel campaignModel : campaigns) {
			CampaignState campaignState = campaignService.getState(campaignModel.getId());
			if(campaignState != null && StatusConstant.POLICY_LAUNCHING.equals(campaignState.getCode())){
				advertisingAmount++;
			}
		}
		bean.setAdvertisingAmount(advertisingAmount);
		//查询项目下活动数
		Integer campaignCount = campaignMapper.selectCampaignCountByProjectId(projectId);
		bean.setTotalAmount(campaignCount);
		// 查询行业
		String industryName = industryMapper.findIndustryNameById(bean.getIndustryId());
		bean.setIndustryName(industryName);
	}

	public List<ProjectVO> listAllProjects(Integer advertiserId, String advertiserName, Integer id, String name, String code, Long startDate,
			Long endDate, String sortKey, String sortType) {

		Map<String, Object> map = new HashMap<>();
		map.put("advertiserId", advertiserId);
		map.put("advertiserName", advertiserName);
		map.put("id", id);
		map.put("name", name);
		map.put("code", code);

		if (sortKey == null || sortKey.isEmpty()) {
			map.put("sortKey", null);
		} else {
			sortKey = CamelCaseUtil.camelToUnderline(sortKey);
			map.put("sortKey", sortKey);
			if (sortType != null && sortType.equals(StatusConstant.SORT_TYPE_ASC)) {
				map.put("sortType", "ASC");
			} else if (sortType != null && sortType.equals(StatusConstant.SORT_TYPE_DESC)) {
				map.put("sortType", "DESC");
			} else {
				throw new IllegalArgumentException(PhrasesConstant.LACK_NECESSARY_PARAM);
			}
		}

		List<ProjectModel> findAllProjects = projectMapper.findAllProjects(map);
		List<ProjectVO> projectVOList = new ArrayList<>();
		for (ProjectModel projectModel : findAllProjects) {
			ProjectVO projectVO = modelMapper.map(projectModel, ProjectVO.class);
			getParam4Bean(projectVO);
			// 查询具体投放信息
			// TODO
			projectVOList.add(projectVO);
		}
		return projectVOList;
	}

	public boolean isUpdateProjectNameExist(ProjectVO project) {
		Map<String,Object> map = new HashMap<>();
		map.put("advertiserId", project.getAdvertiserId());
		map.put("id", project.getId());
		map.put("name", project.getName());
		return projectMapper.findByNotId(map).size() != 0;
	}

	public boolean isUpdateProjectCodeExist(ProjectVO project) {
		Map<String,Object> map = new HashMap<>();
		map.put("id", project.getId());
		map.put("code", project.getCode());
		return projectMapper.findByNotId(map).size() != 0;
	}
}
