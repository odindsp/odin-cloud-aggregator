package com.pxene.odin.cloud.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pxene.odin.cloud.domain.model.UserModel;
import com.pxene.odin.cloud.domain.model.UserModelExample;
import com.pxene.odin.cloud.domain.vo.UserVO;
import com.pxene.odin.cloud.repository.mapper.basic.UserMapper;

/**
 * 用户相关操作的具体实现。
 * @author ningyu
 */
@Service
@Transactional
public class UserService extends BaseService
{
    @Autowired
    private UserMapper userMapper;


    public void saveUser(UserVO user)
    {
        UserModel userModel = modelMapper.map(user, UserModel.class);
        userModel.setId(UUID.randomUUID().toString());
        userMapper.insert(userModel);
    }

    public UserVO deleteUserById(String id)
    {
        if (findById(id) != null)
        {
            userMapper.deleteByPrimaryKey(id);
        }
        return null;
    }

    public void updateUser(UserVO user)
    {
        if (findById(user.getId()) != null)
        {
            UserModel userModel = modelMapper.map(user, UserModel.class);
            userMapper.updateByPrimaryKey(userModel);
        }
    }

    public UserVO findById(String id)
    {
        UserModel userModel = userMapper.selectByPrimaryKey(id);
        UserVO userVO = modelMapper.map(userModel, UserVO.class);
        return userVO;
    }

    public UserVO findByName(String name)
    {
        UserModelExample example = new UserModelExample();
        example.createCriteria().andUsernameLike(name);

        if (userMapper.selectByExample(example).size() > 0)
        {
            UserModel userModel = userMapper.selectByExample(example).get(0);
            return modelMapper.map(userModel, UserVO.class);
        }
        else
        {
            return null;
        }
    }

    public List<UserModel> findAllUsers()
    {
        UserModelExample example = new UserModelExample();

        return userMapper.selectByExample(example);
    }

    public boolean isUserExist(UserVO user)
    {
        return findByName(user.getUsername()) != null;
    }

}
