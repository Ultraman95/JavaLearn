package com.nxquant.exchange.core.microservice.config;

import com.nxquant.exchange.core.microservice.ConsulDiscoveryService;
import com.nxquant.exchange.core.microservice.ConsulProvider;
import com.nxquant.exchange.core.microservice.DiscoveryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ConsulConfiguration.class)
public class DiscoveryConfiguration {

    @Bean
    public DiscoveryService discoveryService(ConsulProvider consulProvider){
        return new ConsulDiscoveryService(consulProvider);
    }
}
