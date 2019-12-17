package com.nxquant.exchange.match.dto;

import org.apache.kafka.clients.consumer.ConsumerRecord;


/**
 * @author shilf
 * Lmax--输入事件类
 */
public final class InputEventData<T extends Info> {
    private String topic;
    private int partition;
    private long offset;
    private T content;
    private long timestamp;

    public void setData(ConsumerRecord record) {
        if(record != null) {
            this.timestamp = record.timestamp();
            this.topic = record.topic();
            this.partition = record.partition();
            this.offset = record.offset();
            this.content = (T) record.value();
        }
    }

    @Override
    public String toString() {
        return "InputEventData{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", timestamp=" + timestamp +
                ", content=" + content +
                '}';
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public T getContent() {
        return content;
    }
}

