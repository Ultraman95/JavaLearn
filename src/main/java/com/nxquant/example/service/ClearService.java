package com.nxquant.example.service;

import com.nxquant.example.core.work.WorkerManager;
import com.nxquant.example.core.work.WorkerManagerService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ClearService implements WorkerManagerService {
    private WorkerManager workerManager;
    private MeterRegistry meterRegistry;

    @Override
    public void setWorkerManager(WorkerManager workerManager){
        this.workerManager = workerManager;
    }

    @Override
    public void setMeterRegistry(MeterRegistry meterRegistry){
        this.meterRegistry = meterRegistry;
    }

    public MeterRegistry getMeterRegistry(){
        return this.meterRegistry;
    }
}
