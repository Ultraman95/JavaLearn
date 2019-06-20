package com.nxquant.example.lifecycle;

import com.nxquant.example.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
            Thread manageThread = new Thread(mainWorker::start);
            manageThread.setDaemon(true);
            manageThread.setName("AppLogic");
            manageThread.start();
        } catch (Exception exp) {
            System.exit(-1);
        }
    }
}
