package com.pxene.odin.cloud.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pxene.odin.cloud.domain.PaginationResponse;
import com.pxene.odin.cloud.domain.model.UserModel;
import com.pxene.odin.cloud.domain.vo.UserVO;
import com.pxene.odin.cloud.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 用 户相关的Web Controller。
 * @author ningyu
 */
@RestController
@RequestMapping(value = "/v1/users")
@Slf4j
public class UserController
{
    @Autowired
    private UserService userService;


    /**
     * 创建用户。
     * @param user  包含用户信息的对象
     * @return HTTP响应码：201，在响应首部的“Location”属性中指定如何获得这个用户。
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody UserVO user)
    {
        log.debug("Creating User {}.", user.getUsername());

        if (userService.isUserExist(user))
        {
            log.debug("A User with username {} already exist.", user.getUsername());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userService.saveUser(user);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("id", user.getId());

        return new ResponseEntity<>(resultMap, HttpStatus.CREATED);
    }

    /**
     * 删除指定ID的用户。
     * @param id    用户ID
     * @return  HTTP响应码：204，没有响应体。
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserVO> deleteUser(@PathVariable String id)
    {
        log.debug("Fetching & Deleting User with id {}.", id);

        UserVO user = userService.findById(id);

        if (user == null)
        {
            log.debug("Unable to delete. User with id {} not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userService.deleteUserById(id);

        return new ResponseEntity<UserVO>(HttpStatus.NO_CONTENT);
    }

    /**
     * 更新指定ID的用户。
     * @param id    用户ID
     * @param user  包含用户信息的对象
     * @return  HTTP响应码200，响应体中包含更新后的用户实体。
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserVO> updateUser(@PathVariable String id, @RequestBody UserModel user)
    {
        log.debug("Updating User {}.", id);

        UserVO currentUser = userService.findById(id);

        if (currentUser == null)
        {
            log.debug("User with id {} not found.", id);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentUser.setUsername(user.getUsername());
        currentUser.setPassword(user.getPassword());
        currentUser.setStatus(user.getStatus());
        currentUser.setPasswordLastUpdatetime(user.getPasswordLastUpdatetime());

        userService.updateUser(currentUser);

        return new ResponseEntity<UserVO>(currentUser, HttpStatus.OK);
    }

    /**
     * 获得全部用户列表。
     * @param pageable  分页参数
     * @return HTTP响应码200，响应体中包含全部用户组成的列表。
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginationResponse> listAllUsers(@PageableDefault(page = 1, size = 1, direction = Direction.DESC) Pageable pageable)
    {
        Page<Object> pager = null;

        if (pageable != null)
        {
            pager = PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize());
        }

        List<UserModel> users = userService.findAllUsers();

        if (users == null || users.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // You many decide to return HttpStatus.NOT_FOUND
        }

        PaginationResponse result = new PaginationResponse(users, pager);

        return new ResponseEntity<PaginationResponse>(result, HttpStatus.OK);
    }

    /**
     * 获取指定ID的用户。
     * @param id    用户ID
     * @return  HTTP响应码200，响应体中包含查询出的用户实体。
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserVO> getUser(@PathVariable String id)
    {
        log.info("Fetching User with id {}.", id);

        UserVO user = userService.findById(id);

        if (user == null)
        {
            log.debug("User with id {} not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<UserVO>(user, HttpStatus.OK);
    }
}
