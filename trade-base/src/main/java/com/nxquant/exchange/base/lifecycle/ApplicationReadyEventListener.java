package com.nxquant.exchange.base.lifecycle;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        System.out.println("$============>>>>> ApplicationReadyEvent is triggered");
        System.out.println(applicationReadyEvent.getTimestamp());
        System.out.println("============>>>>> End");
    }
}
