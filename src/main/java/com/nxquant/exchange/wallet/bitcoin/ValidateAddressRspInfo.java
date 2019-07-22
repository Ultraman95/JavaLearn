package com.nxquant.exchange.wallet.bitcoin;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateAddressRspInfo implements Serializable {
    private Boolean isvalid;
    private String address;
    private String scriptPubKey;
    private Boolean isscript;
    private Boolean iswitness;
    private Float witness_version;
    private String witness_program;
}
