package com.pxene.odin.cloud;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.domain.model.UserModel;
import com.pxene.odin.cloud.repository.mapper.basic.UserMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class UserTest
{
    @Autowired
    private UserMapper userMapper;

    @Test
    @Rollback(value = false)
    public void findByName() throws Exception
    {
        UserModel userModel = new UserModel();
        userModel.setId("19870507");
        userModel.setUsername("tony");
        userModel.setPassword("111111");
        userModel.setStatus(true);
        userModel.setPasswordLastUpdatetime(new Date(1503978268));
        userMapper.insert(userModel);

        UserModel user = userMapper.selectByPrimaryKey("19870507");
        System.out.println("User: " + user);
        Assert.assertEquals("19870507", user.getId());
    }
}
