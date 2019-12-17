package com.nxquant.exchange.base.configure;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CrossDomain implements WebMvcConfigurer {
    //@CrossOrigin这个注解好像也可以做到跨越访问，可以在Controller上定义
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("$============>>>>> AddCorsMappings is triggered");
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*");
        System.out.println(System.currentTimeMillis());
        System.out.println("============>>>>> End");
    }
}
