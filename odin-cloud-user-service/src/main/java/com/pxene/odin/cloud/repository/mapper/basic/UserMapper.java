package com.pxene.odin.cloud.repository.mapper.basic;

import com.pxene.odin.cloud.domain.model.UserModel;
import com.pxene.odin.cloud.domain.model.UserModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface UserMapper {
    @SelectProvider(type=UserModelSqlProvider.class, method="countByExample")
    long countByExample(UserModelExample example);

    @DeleteProvider(type=UserModelSqlProvider.class, method="deleteByExample")
    int deleteByExample(UserModelExample example);

    @Delete({
        "delete from odin_t_user",
        "where id = #{id,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String id);

    @Insert({
        "insert into odin_t_user (id, username, ",
        "password, status, password_last_updatetime)",
        "values (#{id,jdbcType=VARCHAR}, #{username,jdbcType=VARCHAR}, ",
        "#{password,jdbcType=VARCHAR}, #{status,jdbcType=BIT}, #{passwordLastUpdatetime,jdbcType=TIMESTAMP})"
    })
    int insert(UserModel record);

    @InsertProvider(type=UserModelSqlProvider.class, method="insertSelective")
    int insertSelective(UserModel record);

    @SelectProvider(type=UserModelSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.BIT),
        @Result(column="password_last_updatetime", property="passwordLastUpdatetime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<UserModel> selectByExample(UserModelExample example);

    @Select({
        "select",
        "id, username, password, status, password_last_updatetime",
        "from odin_t_user",
        "where id = #{id,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR),
        @Result(column="status", property="status", jdbcType=JdbcType.BIT),
        @Result(column="password_last_updatetime", property="passwordLastUpdatetime", jdbcType=JdbcType.TIMESTAMP)
    })
    UserModel selectByPrimaryKey(String id);

    @UpdateProvider(type=UserModelSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") UserModel record, @Param("example") UserModelExample example);

    @UpdateProvider(type=UserModelSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") UserModel record, @Param("example") UserModelExample example);

    @UpdateProvider(type=UserModelSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(UserModel record);

    @Update({
        "update odin_t_user",
        "set username = #{username,jdbcType=VARCHAR},",
          "password = #{password,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=BIT},",
          "password_last_updatetime = #{passwordLastUpdatetime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(UserModel record);
}