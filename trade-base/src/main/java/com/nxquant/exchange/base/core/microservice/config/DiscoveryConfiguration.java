package com.nxquant.exchange.base.core.microservice.config;

import com.nxquant.exchange.base.core.microservice.ConsulDiscoveryService;
import com.nxquant.exchange.base.core.microservice.ConsulProvider;
import com.nxquant.exchange.base.core.microservice.DiscoveryService;
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
