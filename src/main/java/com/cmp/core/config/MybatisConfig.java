package com.cmp.core.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.cmp.core.*.dao")
public class MybatisConfig {
}
