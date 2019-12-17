package com.nxquant.exchange.base.core.work;

import io.micrometer.core.instrument.MeterRegistry;

public interface WorkerManagerService {
    void setWorkerManager(WorkerManager workerManager);

    void setMeterRegistry(MeterRegistry meterRegistry);
}
