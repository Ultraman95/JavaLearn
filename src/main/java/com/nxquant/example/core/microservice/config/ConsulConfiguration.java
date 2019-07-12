package com.nxquant.example.core.microservice.config;

import com.nxquant.example.core.microservice.ConsulProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulConfiguration {
    @Bean
    @ConfigurationProperties("com.nxquant.microservice.consul")
    public ConsulProperties consulProperties() {
        return new ConsulProperties();
    }

    @Bean
    public ConsulProvider consulProvider(ConsulProperties consulProperties){
        return new ConsulProvider(consulProperties);
    }
}
