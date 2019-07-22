package com.nxquant.exchange.wallet.bitcoin;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateAddressReqInfo implements Serializable {
    private String address;
}
