package com.nxquant.example.core.kryo;


import org.apache.kafka.common.serialization.Serializer;
import java.util.Map;

public class KryoSerializer<T> implements Serializer<T> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, Object object) {
        return KryoUtils.serialize(object);
    }

    @Override
    public void close() {

    }
}
