package jshop.web;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@ComponentScan(basePackages = {"jshop.core"})
@EnableCaching
public class InitConfig {}
