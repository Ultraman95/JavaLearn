package com.nxquant.exchange.core.work;

import org.apache.kafka.common.TopicPartition;

public class IncState {
    TopicPartition incTp;

    /**
     * 对应相关全量快照的位置
     */
    long relatedSnapOffset;

    /**
     * 打此增量快照时，对应数据输入源的Offset
     */
    private long sourceOffset;

}
