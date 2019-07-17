package com.nxquant.exchange.core.work;

/**
 * 与Worker在Consul服务注册有关系
 * 监听Worker的移除和添加
 * 在Consul中，是以Worker为单位的
 * 在场上系统中，也是以Worker为单位的，一个Worker对应(一个Topic的其中一个Partition)
 */
public interface WorkerListener {
}
