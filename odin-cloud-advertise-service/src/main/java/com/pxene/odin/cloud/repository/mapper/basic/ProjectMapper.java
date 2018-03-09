package com.pxene.odin.cloud.repository.mapper.basic;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.pxene.odin.cloud.domain.model.ProjectModel;

public interface ProjectMapper {

	@UpdateProvider(type = ProjectModelSqlProvider.class, method = "updateByIdSelective")
	int updateByIdSelective(ProjectModel record);

	@Select({ "select", "id, name, code, advertiser_id, industry_id, capital, create_user, create_time, ",
			"update_user, update_time", "from tb_project", "where id = #{id,jdbcType=INTEGER}" })
	ProjectModel selectProjectById(Integer id);

	@Options(useGeneratedKeys = true)
	@Insert({ "insert into tb_project (id, name, ", "code, advertiser_id, ", "industry_id, capital, ",
			"create_user, update_user)",
			"values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
			"#{code,jdbcType=VARCHAR}, #{advertiserId,jdbcType=INTEGER}, ",
			"#{industryId,jdbcType=INTEGER}, #{capital,jdbcType=BIGINT}, ",
			"#{createUser,jdbcType=INTEGER}, #{updateUser,jdbcType=INTEGER})" })
	int insert(ProjectModel record);

	@Select({ "select", "id, name, code, advertiser_id, industry_id, capital, create_user, create_time, ",
			"update_user, update_time", "from tb_project", "where name = #{name,jdbcType=VARCHAR} ",
			"and advertiser_id = #{advertiserId,jdbcType=INTEGER}" })
	List<ProjectModel> selectProjectByNameAndAdvertiserId(@Param("name") String name,@Param("advertiserId") Integer advertiserId);

	@Select({ "select", "id, name, code, advertiser_id, industry_id, capital, create_user, create_time, ",
			"update_user, update_time", "from tb_project", "where code = #{code,jdbcType=VARCHAR}" })
	List<ProjectModel> selectProjectByCode(String code);
	
	@SelectProvider(type = ProjectModelSqlProvider.class, method = "findAllProjects")
	List<ProjectModel> findAllProjects(Map<String,Object> record);
	
	@SelectProvider(type = ProjectModelSqlProvider.class, method = "selectByNotId")
	List<ProjectModel> findByNotId(Map<String, Object> map);
}