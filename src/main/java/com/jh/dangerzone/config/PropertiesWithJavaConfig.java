package com.jh.dangerzone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/*
Adds another property file to the classpath making it available
at startup
 */
@Configuration
@PropertySource("classpath:config.properties")
public class PropertiesWithJavaConfig {

}
