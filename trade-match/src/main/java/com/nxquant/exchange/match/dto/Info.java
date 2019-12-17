package com.nxquant.exchange.match.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public interface Info extends Serializable {
    @JsonIgnore
    default String getFeature() {
        return getClass().getCanonicalName();
    }
}

