package com.nxquant.exchange.core.microservice;

import com.orbitz.consul.Consul;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ConsulDiscoveryService implements DiscoveryService, ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext applicationContext;
    private final Consul consul;

    public ConsulDiscoveryService(ConsulProvider consulProvider){
        this.consul = consulProvider.getConsul();
    }

    @Override
    public void subscribeService(ServiceCoordinate... serviceCoordinates){

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
