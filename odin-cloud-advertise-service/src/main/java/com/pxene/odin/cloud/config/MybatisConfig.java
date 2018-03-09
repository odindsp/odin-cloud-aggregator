package com.pxene.odin.cloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.pxene.odin.cloud.repository")
public class MybatisConfig
{

}
