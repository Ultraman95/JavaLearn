package com.nxquant.exchange.base.core.kryo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface KryoInfo extends Serializable {
    @JsonIgnore
    default String getKryoName(){
        return getClass().getCanonicalName();
    }
}
