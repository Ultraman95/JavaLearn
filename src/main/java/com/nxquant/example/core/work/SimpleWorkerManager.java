package com.nxquant.example.core.work;

public class SimpleWorkerManager implements WorkerManager {
    private WorkerContext workerContext;

    public SimpleWorkerManager(WorkerContext workerContext){
        this.workerContext = workerContext;
    }

    @Override
    public void start(){

    }
}
