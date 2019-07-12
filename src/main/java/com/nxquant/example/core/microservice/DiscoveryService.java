package com.nxquant.example.core.microservice;

public interface DiscoveryService {
    void subscribeService(ServiceCoordinate... serviceCoordinates);
}
