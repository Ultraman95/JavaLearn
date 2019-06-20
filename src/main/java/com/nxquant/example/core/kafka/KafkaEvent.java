package com.nxquant.example.core.kafka;

import com.nxquant.example.core.kryo.KryoInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.Serializable;

public class KafkaEvent<T extends KryoInfo> implements Serializable {
    private String topic;
    private int partition;
    private long offset;
    private T info;
    private long timestamp;
    private boolean endOfBatch = true;

    public KafkaEvent() {
    }

    @SuppressWarnings("unchecked")
    public KafkaEvent(ConsumerRecord record) {
        if (record != null) {
            this.topic = record.topic();
            this.partition = record.partition();
            this.offset = record.offset();
            this.timestamp = record.timestamp();
            this.info = (T) record.value();
        }
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

    public T getInfo() {
        return info;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isEndOfBatch() {
        return endOfBatch;
    }

    public void setEndOfBatch(boolean endOfBatch) {
        this.endOfBatch = endOfBatch;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KafkaEvent:{");
        sb.append("topic='").append(topic).append('\'');
        sb.append(", partition=").append(partition);
        sb.append(", offset=").append(offset);
        sb.append(", info=").append(info);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", endOfBatch=").append(endOfBatch);
        sb.append('}');
        return sb.toString();
    }
}
