package com.nxquant.exchange.lifecycle;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//我们可以通过Order注解或者使用Ordered接口来指定调用顺序，@Order()中的值越小，优先级越高
@Component
@Order(1)
public class CommandRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("$============>>>>> CommandRunner is triggered");
        System.out.println(System.currentTimeMillis());
        System.out.println("============>>>>> End");
    }
}
