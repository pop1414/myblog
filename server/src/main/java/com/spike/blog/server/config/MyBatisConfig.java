package com.spike.blog.server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author DDY
 * @version 1.0
 * @date 1/16/2026-3:35 PM
 * @description com.spike.blog.server.config
 */
@Configuration
@MapperScan({"com.spike.blog.server.dao", "com.spike.blog.server.mapper"})
public class MyBatisConfig {

}
