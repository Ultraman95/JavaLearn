package com.nxquant.exchange.base.lifecycle;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        System.out.println("$============>>>>> ApplicationEnvironmentPreparedEvent is triggered");
        System.out.println(applicationEnvironmentPreparedEvent.getTimestamp());
        System.out.println("============>>>>> End");
    }
}
