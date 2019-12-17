package com.nxquant.exchange.match.core;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author oumingzhi
 * @description 可命名线程工厂.
 * @date 2019/9/20
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final String NAME_PATTERN = "%s-%d";
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String threadNamePrefix;
    private boolean isDaemon;

    public NamedThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public NamedThreadFactory(String threadNamePrefix, boolean isDaemon) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.threadNamePrefix = checkPrefix(threadNamePrefix);
        this.isDaemon = isDaemon;
    }

    private static String checkPrefix(String prefix) {
        return prefix != null && prefix.length() != 0 ? prefix : "platform";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = String.format(Locale.ROOT, NAME_PATTERN, threadNamePrefix, threadNumber.getAndIncrement());
        Thread thread = new Thread(group, runnable, name, 0L);
        thread.setDaemon(isDaemon);
        thread.setPriority(5);
        return thread;
    }
}
