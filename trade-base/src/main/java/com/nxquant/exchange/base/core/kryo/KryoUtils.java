package com.nxquant.exchange.base.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;


public class KryoUtils {
    private static final Logger logger = LoggerFactory.getLogger(KryoUtils.class);

    private static final KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            try {
                UnmodifiableCollectionsSerializer.registerSerializers(kryo);
                SynchronizedCollectionsSerializer.registerSerializers(kryo);
            } catch (Exception e) {
                logger.error("Exception occurred", e);
            }
            return kryo;
        }
    };

    private static final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();


    public static byte[] serialize(final Object obj) {

        return pool.run(new KryoCallback<byte[]>() {

            @Override
            public byte[] execute(Kryo kryo) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Output output = new Output(stream);
                kryo.writeClassAndObject(output, obj);
                output.close();
                return stream.toByteArray();
            }

        });
    }

    @SuppressWarnings("unchecked")
    public static <V> V deserialize(final byte[] objectData) {

        return (V) pool.run(new KryoCallback<V>() {

            @Override
            public V execute(Kryo kryo) {
                Input input = new Input(objectData);
                return (V) kryo.readClassAndObject(input);
            }

        });
    }

    public static <V> V deepCopy(final V obj) {

        return (V) pool.run(new KryoCallback<V>() {

            @Override
            public V execute(Kryo kryo) {
                return (V) kryo.copy(obj);
            }
        });
    }
}
