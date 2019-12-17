package com.nxquant.exchange.base.core.work;

import com.nxquant.exchange.base.entity.SnapMemoryStorage;
import org.apache.kafka.common.TopicPartition;

public class SnapState {
    TopicPartition snapTp;

    SnapMemoryStorage snapMemoryStorage;
}
