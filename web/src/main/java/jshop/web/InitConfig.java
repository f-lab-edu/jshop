package jshop.web;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@ComponentScan(basePackages = {"jshop.common", "jshop.core"})
@EnableCaching
public class InitConfig {}
