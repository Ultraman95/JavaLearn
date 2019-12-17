package com.nxquant.exchange.base.lifecycle;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 总的事件监听,可以去掉其余监听
 */
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ApplicationStartedEvent){
            //获取上下文
            ApplicationContext applicationContext = ((ApplicationStartedEvent) event).getApplicationContext();
            System.out.println("$============>>>>> ApplicationEventListener is triggered, ApplicationStartedEvent");
            System.out.println(event.getTimestamp());
            System.out.println("============>>>>> End");
        }else if(event instanceof ContextClosedEvent){
            //程序异常的处理
            stop();
        }
    }

    private void stop(){
        System.out.println("Application Stop !");
    }
}
