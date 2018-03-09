package com.pxene.odin.cloud.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.common.constant.PhrasesConstant;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.model.ProjectModel;
import com.pxene.odin.cloud.domain.vo.ProjectVO;
import com.pxene.odin.cloud.exception.DuplicateEntityException;
import com.pxene.odin.cloud.exception.IllegalArgumentException;
import com.pxene.odin.cloud.exception.ResourceNotFoundException;
import com.pxene.odin.cloud.service.ProjectService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProjectController {
	@Autowired
	private ProjectService projectService;
	
	/**
	 * 
	 * @param advertiserName
	 * @param id
	 * @param name
	 * @param code
	 * @param startDate
	 * @param endDate
	 * @param sortKey
	 * @param sortType
	 * @param pageNo
	 * @param pageSize
	 * @return HTTP响应码200,204
	 */
	@GetMapping(value="/projects", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaginationResponse> listAllProject(
			@RequestParam(required = false) Integer advertiserId,@RequestParam(required = false) String advertiserName,
			@RequestParam(required = false) Integer id, @RequestParam(required = false) String name,
			@RequestParam(required = false) String code, @RequestParam(required = false) Long startDate,
			@RequestParam(required = false) Long endDate, @RequestParam(required = false) String sortKey,
			@RequestParam(required = false) String sortType, @RequestParam(required = false) Integer pageNo,
			@RequestParam(required = false) Integer pageSize) {
		Page<Object> pager = null;
		if (pageNo != null && pageSize != null){
		pager = PageHelper.startPage(pageNo, pageSize);
		}
		
		List<ProjectVO> projects = projectService.listAllProjects(advertiserId,advertiserName,id,name,code,startDate,endDate,sortKey,sortType);

//		if (projects == null || projects.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//		}

		PaginationResponse result = new PaginationResponse(projects, pager);

		return ResponseEntity.ok(result);

	}
	
	/**
	 * 获取指定ID的项目。
	 * 
	 * @param id 项目ID
	 *            
	 * @return HTTP响应码200，404  响应体中包含查询出的项目实体。
	 */
	@GetMapping(value = "/project/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProjectVO> getProject(@PathVariable Integer id) {
		log.info("Fetching Project with id {}.", id);

		ProjectModel project = projectService.findById(id);

		if (project == null) {
			log.debug("Project with id {} not found.", id);
			throw new ResourceNotFoundException(PhrasesConstant.PROJECT_NOT_FOUND);
		}
		ProjectVO projectVO=projectService.getProjectVO(project);

		return ResponseEntity.ok(projectVO);
	}
	
	/**
	 * 编辑项目
	 * 
	 * @param id 项目ID
	 *            
	 * @param project 包含项目信息的对象
	 *            
	 * @return HTTP响应码：200,404
	 */
	@PutMapping(value = "/project/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateProject(@PathVariable Integer id, @RequestBody ProjectVO project) {
		log.debug("Updating Project {}.", id);
		project.setId(id);
		ProjectModel currentProject = projectService.findById(id);

		if (currentProject == null) {
			log.debug("A Project with id {} not found.", id);
			throw new ResourceNotFoundException(PhrasesConstant.PROJECT_NOT_FOUND);
		}
		if (projectService.isUpdateProjectNameExist(project)) {
			log.debug("A Project with name {} already exist.", project.getName());
			throw new IllegalArgumentException(PhrasesConstant.NAME_IS_EXIST);
		}

		if (projectService.isUpdateProjectCodeExist(project)) {
			log.debug("A Project with code {} already exist.", project.getCode());
			throw new IllegalArgumentException(PhrasesConstant.CODE_IS_EXIST);
		}
		currentProject.setAdvertiserId(null);
		currentProject.setName(project.getName());
		currentProject.setCode(project.getCode());
		currentProject.setIndustryId(project.getIndustryId());
		currentProject.setCapital(project.getCapital());

		projectService.updateProject(currentProject);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}
	
	/**
	 * 创建项目。
	 * 
	 * @param project
	 *            包含项目信息的对象
	 * @return HTTP响应码：201,409,响应体id表示新建项目的ID
	 */
	@PostMapping(value="/project", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> createProject(@Valid @RequestBody ProjectVO project) {
		log.debug("Creating Project {}.", project.getName());

		if (projectService.isProjectNameExist(project)) {
			log.debug("A Project with name {} already exist.", project.getName());
			throw new DuplicateEntityException(PhrasesConstant.NAME_IS_EXIST);
		}

		if (projectService.isProjectCodeExist(project)) {
			log.debug("A Project with code {} already exist.", project.getCode());
			throw new DuplicateEntityException(PhrasesConstant.CODE_IS_EXIST);
		}
		
		ProjectModel saveProject = projectService.saveProject(project);
		Map<String, Object> result = new HashMap<>();
		result.put("id", saveProject.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}
}
