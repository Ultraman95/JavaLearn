package com.nxquant.example.lifecycle;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
        System.out.println("$============>>>>> ApplicationPreparedEvent is triggered");
        System.out.println(applicationPreparedEvent.getTimestamp());
        System.out.println("============>>>>> End");
    }
}
