package com.nxquant.example.lifecycle;

import com.nxquant.example.core.work.SimpleWorkerManager;
import com.nxquant.example.core.work.WorkerContext;
import com.nxquant.example.core.work.WorkerManager;
import com.nxquant.example.core.work.WorkerManagerService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    private WorkerManager workerManager;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.println("$============>>>>> ApplicationStartedEvent is triggered");
        System.out.println(applicationStartedEvent.getTimestamp());
        System.out.println("============>>>>> End");
        initApplication(applicationStartedEvent.getApplicationContext());
    }

    private void initApplication(ApplicationContext context){
        WorkerContext workerContext = createWorkerContext(context);
        this.workerManager = new SimpleWorkerManager(workerContext);

        //对需要用到workerManager的service注入workerManager
        context.getBeansOfType(WorkerManagerService.class).forEach((serviceName, serviceBean) -> serviceBean.setWorkerManager(workerManager));

        //启动workerManager
        this.workerManager.start();
    }

    private WorkerContext createWorkerContext(ApplicationContext context){
        /**
         * 此处主要组装一些Worker的上下文信息(将CustomConfiguration信息统一过来)
         */
        return new WorkerContext();
    }

}
