package com.nxquant.example.service;

import com.nxquant.example.core.work.WorkerManager;
import com.nxquant.example.core.work.WorkerManagerService;
import org.springframework.stereotype.Service;

@Service
public class ClearService implements WorkerManagerService {
    private WorkerManager workerManager;

    @Override
    public void setWorkerManager(WorkerManager workerManager){
        this.workerManager = workerManager;
    }
}
