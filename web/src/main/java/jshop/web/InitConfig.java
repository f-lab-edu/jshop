package jshop.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"jshop.common", "jshop.core"})
public class InitConfig {}
