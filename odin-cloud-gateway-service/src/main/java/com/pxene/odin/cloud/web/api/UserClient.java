package com.pxene.odin.cloud.web.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pxene.odin.cloud.web.api.fallback.UserClientFallback;

@FeignClient(name = "ODIN-CLOUD-USER-SERVICE", fallback = UserClientFallback.class)
public interface UserClient
{
    @RequestMapping(value = "/v1/users/{id}", method = RequestMethod.GET)
    public String findUser(@PathVariable("id") String id);
}
