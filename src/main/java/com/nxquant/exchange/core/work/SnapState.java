package com.nxquant.exchange.core.work;

import com.nxquant.exchange.entity.SnapMemoryStorage;
import org.apache.kafka.common.TopicPartition;

public class SnapState {
    TopicPartition snapTp;

    SnapMemoryStorage snapMemoryStorage;
}
