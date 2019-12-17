package com.nxquant.exchange.match.configure;

import com.nxquant.exchange.match.core.MatchService;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author shilf
 * Match运行上下文
 */
@Configuration
public class WorkContext {
    private final String PREFIX = "com.nxquant.exchange.match";
    private final String PREFIX_KAFKA_INPUT_CONSUMER = PREFIX + ".input.consumer";
    private final String PREFIX_KAFKA_SNAP_CONSUMER  = PREFIX + ".snap.consumer";
    private final String PREFIX_KAFKA_INC_CONSUMER  = PREFIX + ".inc.consumer";

    private Properties inputConsumerProp = new Properties();
    private Properties snapConsumerProp = new Properties();
    private Properties incConsumerProp = new Properties();

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration pollTimeout;

    private String inputTopic;
    private String snapTopic;
    private String incTopic;

    private int transActionOffset = 2;
    private int tryTimes = 3;

    private int commitRtnSize = 50;
    private int commitReceiveSize = 4;

    private Map<String, TopicPartition> instrumentTpMap = new HashMap<>();

    @Autowired
    MatchService matchService;

    public Properties getInputConsumerProp() {
        return inputConsumerProp;
    }

    public Properties getSnapConsumerProp() {
        return snapConsumerProp;
    }

    public Properties getIncConsumerProp() {
        return incConsumerProp;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getSnapTopic() {
        return snapTopic;
    }

    public void setSnapTopic(String snapTopic) {
        this.snapTopic = snapTopic;
    }

    public String getIncTopic() {
        return incTopic;
    }

    public void setIncTopic(String incTopic) {
        this.incTopic = incTopic;
    }

    public Duration getPollTimeout() {
        return pollTimeout;
    }

    public void setPollTimeout(Duration pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public int getTransActionOffset() {
        return transActionOffset;
    }

    public int getTryTimes() {
        return tryTimes;
    }


    public int getCommitRtnSize() {
        return commitRtnSize;
    }

    public int getCommitReceiveSize() {
        return commitReceiveSize;
    }

    public Map<String, TopicPartition> getInstrumentTpMap() {
        return instrumentTpMap;
    }

    @Bean
    @ConfigurationProperties(prefix = PREFIX + ".global")
    public WorkContext loadProperties(Environment environment) {
        extractPropertiesConfig(environment, PREFIX_KAFKA_INPUT_CONSUMER, getInputConsumerProp());
        extractPropertiesConfig(environment, PREFIX_KAFKA_SNAP_CONSUMER, getSnapConsumerProp());
        extractPropertiesConfig(environment, PREFIX_KAFKA_INC_CONSUMER, getIncConsumerProp());
        return this;
    }

    private void extractPropertiesConfig(Environment environment, String prefix, Properties properties) {
        Binder.get(environment).bind(prefix, Bindable.mapOf(String.class, String.class))
                .ifBound(map -> map.entrySet().forEach(entry -> properties.merge(entry.getKey(), entry.getValue(), (ov, nv) -> nv)));
    }

    public MatchService getMatchService() {
        return matchService;
    }
}
