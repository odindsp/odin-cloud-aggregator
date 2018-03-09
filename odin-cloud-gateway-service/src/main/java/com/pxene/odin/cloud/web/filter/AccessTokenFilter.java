package com.pxene.odin.cloud.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.pxene.odin.cloud.web.api.UserClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessTokenFilter extends ZuulFilter
{
    @Autowired
    private UserClient userClient;


    @Override
    public boolean shouldFilter()
    {
        return true;
    }

    @Override
    public Object run()
    {
        RequestContext currentContext = RequestContext.getCurrentContext();

        HttpServletRequest request = currentContext.getRequest();

        log.debug(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        String authorization = request.getHeader("Authorization");


        StringBuffer requestURL = request.getRequestURL();

        String findedUser = userClient.findUser("123");
        log.debug(String.format("### TONY ### > Request URL is: %s", requestURL));
        log.debug(String.format("### TONY ### > Find result is %s", findedUser));

        if (StringUtils.isEmpty(authorization))
        {
            log.warn("access token is empty");
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        log.info("access token ok");

        return null;
    }

    @Override
    public String filterType()
    {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder()
    {
        return 0;
    }

}
