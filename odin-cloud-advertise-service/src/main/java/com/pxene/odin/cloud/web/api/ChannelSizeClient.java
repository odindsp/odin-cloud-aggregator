package com.pxene.odin.cloud.web.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created on 2017/9/21.
 */
@FeignClient(name = "ODIN-CLOUD-CHANNEL-SERVICE")
public interface ChannelSizeClient {

    @RequestMapping(value = "/size/{id}", method = RequestMethod.GET)
    public String selectSizeById(@PathVariable("id")Integer id);

}
