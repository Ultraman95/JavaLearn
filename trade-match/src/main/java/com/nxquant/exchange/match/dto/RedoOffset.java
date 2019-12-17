package com.nxquant.exchange.match.dto;

import org.apache.kafka.common.TopicPartition;

/**
 * @author shilf
 * 反演区间
 */
public class RedoOffset {
    private TopicPartition topicPartition;
    private long minOffset;
    private long maxOffset;


    public RedoOffset(TopicPartition topicPartition, long minOffset, long maxOffset){
        this.topicPartition = topicPartition;
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
    }

    public TopicPartition getTopicPartition() {
        return topicPartition;
    }

    public void setTopicPartition(TopicPartition topicPartition) {
        this.topicPartition = topicPartition;
    }

    public long getMinOffset() {
        return minOffset;
    }

    public void setMinOffset(long minOffset) {
        this.minOffset = minOffset;
    }

    public long getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(long maxOffset) {
        this.maxOffset = maxOffset;
    }
}
