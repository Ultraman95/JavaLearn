package com.nxquant.exchange.base.lifecycle;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent applicationFailedEvent) {
        //程序启动失败时，触发的
        System.out.println("$============>>>>> ApplicationFailedEvent is triggered");
        System.out.println(applicationFailedEvent.getTimestamp());
        System.out.println("============>>>>> End");
    }
}
