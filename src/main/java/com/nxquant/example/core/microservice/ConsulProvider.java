package com.nxquant.example.core.microservice;

import com.google.common.net.HostAndPort;
import com.nxquant.example.core.microservice.config.ConsulProperties;
import com.orbitz.consul.Consul;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ConsulProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ConsulProperties consulProperties;
    private Consul consul;

    public ConsulProvider(ConsulProperties consulProperties){
        try {
            this.consulProperties = Objects.requireNonNull(consulProperties, "Consul服务配置属性不能null !");
            HostAndPort hostAndPort = HostAndPort.fromString(consulProperties.getHost() + ":" + consulProperties.getPort());
            this.consul = Consul.builder().withHostAndPort(hostAndPort).build();
        }catch (Exception e){
            logger.error("ERROR: new ConsulProvider !", e);
        }
    }

    public ConsulProperties getConsulProperties() {
        return consulProperties;
    }

    public Consul getConsul() {
        return consul;
    }
}
