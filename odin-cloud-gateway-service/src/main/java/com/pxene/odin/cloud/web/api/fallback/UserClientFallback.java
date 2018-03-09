package com.pxene.odin.cloud.web.api.fallback;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.pxene.odin.cloud.web.api.UserClient;

@Component
public class UserClientFallback implements UserClient
{
    public String findUser(@PathVariable("id") String id)
    {
        return "Could not found user of id: " + id + ".";
    }
}
