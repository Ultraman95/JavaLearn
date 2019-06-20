package com.nxquant.example.lifecycle;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
        System.out.println("$============>>>>> ApplicationStartingEvent is triggered");
        System.out.println(applicationStartingEvent.getTimestamp());
        System.out.println("============>>>>> End");
    }
}

