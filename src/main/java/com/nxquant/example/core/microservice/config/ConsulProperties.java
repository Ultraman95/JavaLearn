package com.nxquant.example.core.microservice.config;

public class ConsulProperties {
    private String healthInterval;
    private String host;
    private int port;

    public String getHealthInterval() {
        return healthInterval;
    }

    public void setHealthInterval(String healthInterval) {
        this.healthInterval = healthInterval;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
