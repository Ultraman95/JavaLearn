package com.nxquant.exchange.base.core.microservice;

public interface DiscoveryService {
    void subscribeService(ServiceCoordinate... serviceCoordinates);
}
