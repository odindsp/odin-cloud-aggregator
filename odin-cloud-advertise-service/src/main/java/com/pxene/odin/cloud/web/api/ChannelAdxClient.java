package com.pxene.odin.cloud.web.api;

import java.util.Date;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created on 2017/9/21.
 */
@FeignClient(name = "ODIN-CLOUD-CHANNEL-SERVICE")
public interface ChannelAdxClient {

    @RequestMapping(value = "/adx/{id}", method = RequestMethod.GET)
    public String selectAdxById(@PathVariable("id")Integer id);

    @RequestMapping(value = "/contract/{id}", method = RequestMethod.GET)
    public String selectContractById(@PathVariable("id")Integer id);
    
    @RequestMapping(value = "/contract/{todayStart}", method = RequestMethod.GET)
    public String selectContractByTodayStart(@PathVariable("todayStart")Date todayStart);
    
    @RequestMapping(value = "/contract/{yesterdayEnd}", method = RequestMethod.GET)
    public String selectContractByYesterdayEnd(@PathVariable("yesterdayEnd")Date yesterdayEnd);
}
