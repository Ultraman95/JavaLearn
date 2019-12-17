package com.nxquant.exchange.base.lifecycle;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nxquant.exchange.base.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class AppRunner implements ApplicationRunner {
    @Autowired
    MainService mainWorker;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("$============>>>>> AppRunner is triggered");
        System.out.println(System.currentTimeMillis());
        System.out.println("============>>>>> End");

        try {
            //Thread manageThread = new Thread(mainWorker::start);
            //manageThread.setDaemon(true);
            //manageThread.setName("AppLogic");
            //manageThread.start();
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("AppLogic-%d").build();
            ExecutorService executorService = new ThreadPoolExecutor(1,1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(1024),namedThreadFactory);
            executorService.execute(mainWorker::start);
        } catch (Exception exp) {
            System.exit(-1);
        }

        //此处还可以创建一个风控线程--专门处理爆仓等等
    }
}
