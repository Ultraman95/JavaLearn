package com.nxquant.exchange.core.microservice;

public interface DiscoveryService {
    void subscribeService(ServiceCoordinate... serviceCoordinates);
}
