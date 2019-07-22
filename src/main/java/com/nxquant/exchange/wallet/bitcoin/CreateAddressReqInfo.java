package com.nxquant.exchange.wallet.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 创建地址请求信息
 * label  默认为""
 * accountType  地址类型，可以是legacy、p2sh-segwit和bech32
 * @author shilf
 */
@Data
public class CreateAddressReqInfo implements Serializable {
    private String label;
    @JsonProperty("address_type")
    private String addressType;
}
