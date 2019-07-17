package com.nxquant.exchange.core.kafka;

public class ExKafkaEvent {
    private KafkaEvent event;

    public KafkaEvent getEvent() {
        return event;
    }

    public void setEvent(KafkaEvent event) {
        this.event = event;
    }

    @Override
    public String toString(){
        final StringBuffer sb = new StringBuffer("ExKafkaEvent:{");
        sb.append("event=").append(event);
        sb.append('}');
        return sb.toString();
    }
}
