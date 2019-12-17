package com.nxquant.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class SimilarMain {

        //@Configuration的注解类标识这个类可以使用Spring IoC容器作为bean定义的来源.
        //@Bean注解告诉Spring，一个带有@Bean的注解方法将返回一个对象，该对象应该被注册为在Spring应用程序上下文中的bean.

        //@ComponentScan的功能其实就是自动扫描并加载符合条件的组件（比如@Component和@Repository等）或者bean定义，最终将这些bean定义加载到IoC容器中.

        //@EnableAutoConfiguration(Spring Boot核心)也只是统一了常用的，对于特殊场景的，还需要自定义方式
        //@EnableAutoConfiguration与SpringFactoriesLoader关系密切
        //@EnableAutoConfiguration也是借助@Import的帮助，将所有符合自动配置条件的bean定义加载到IoC容器，仅此而已！
        //@EnableAutoConfiguration会根据类路径中的jar依赖为项目进行自动配置，如：添加了spring-boot-starter-web依赖，会自动添加Tomcat和Spring MVC的依赖，Spring Boot会对Tomcat和Spring MVC进行自动配置.
        public static void main(String[] args) {
            SpringApplication.run(SimilarMain.class, args);
        }
}
