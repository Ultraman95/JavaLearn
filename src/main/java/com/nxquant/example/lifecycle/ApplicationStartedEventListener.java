package com.nxquant.example.lifecycle;

import com.nxquant.example.core.work.SimpleWorkerManager;
import com.nxquant.example.core.work.WorkerContext;
import com.nxquant.example.core.work.WorkerManager;
import com.nxquant.example.core.work.WorkerManagerService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 这个Listener不需要任何配置，也会被触发。其余的Listeners必须在spring.factories中配置
 */
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    private WorkerManager workerManager;
    private MeterRegistry meterRegistry;

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
        this.meterRegistry = context.getBean(MeterRegistry.class);

        //向服务注册单例
        registerToService(context);

        //启动workerManager
        this.workerManager.start();
    }

    private void registerToService(ApplicationContext context){
        //对需要用到workerManager的service注入workerManager
        //对需要用到meterRegistry的service注入meterRegistry
        context.getBeansOfType(WorkerManagerService.class).forEach((serviceName, serviceBean) -> serviceBean.setWorkerManager(workerManager));
        context.getBeansOfType(WorkerManagerService.class).forEach((serviceName, serviceBean) -> serviceBean.setMeterRegistry(meterRegistry));
    }

    private WorkerContext createWorkerContext(ApplicationContext context){
        /**
         * 此处主要组装一些Worker的上下文信息(将CustomConfiguration信息统一过来)
         */
        return new WorkerContext();
    }

}
