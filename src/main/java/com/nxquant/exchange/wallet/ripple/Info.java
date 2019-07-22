package com.nxquant.exchange.wallet.ripple;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;


class Info {
    @JsonProperty("error")
    public String error;

    @JsonProperty("error_code")
    public int errorcode;

    public boolean isValid = false;

    @JsonSetter("status")
    public void setStatus(String status) {
        if (status.equals("success")) {
            this.isValid = true;
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class LedgerInfo extends Info {
    @JsonProperty("ledger_hash")
    public String hash = "";

    @JsonProperty("ledger_index")
    public long index = 0;

    @JsonProperty("validated")
    public boolean validated;

    @JsonProperty("ledger")
    public Ledger ledger;
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Ledger {
    @JsonProperty("transactions")
    public ArrayList<TxPayment> txs = new ArrayList<TxPayment>();
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Account {
    public Long balance;

    @JsonSetter("Balance")
    public void setBalance(String sBalance) {
        this.balance = Long.parseLong(sBalance);
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class AccountInfo extends Info {
    @JsonProperty("account_data")
    public Account account = new Account();
}

@JsonIgnoreProperties(ignoreUnknown=true)
class KeyInfo extends Info {
    @JsonProperty("account_id")
    public String address = "";

    @JsonProperty("key_type")
    public String keyType = "";

    @JsonProperty("master_key")
    public String masterKey = "";

    @JsonProperty("master_seed")
    public String masterSeed = "";

    @JsonProperty("public_key")
    public String publicKey = "";
}

@JsonIgnoreProperties(ignoreUnknown=true)
class TxBlobInfo extends Info {
    @JsonProperty("tx_blob")
    public String blob = "";
}

class TxInfo extends TxBlobInfo {
    @JsonProperty("tx_json")
    public Tx tx = new Tx();
}

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
class Tx {
    @JsonProperty("TransactionType")
    public String type = "";

    @JsonProperty("Sequence")
    public Integer sequence;

    @JsonProperty("hash")
    public String hash;

    public Long fee;

    @JsonGetter("Fee")
    public String getFee() {
        if (fee == null) {
            return null;
        }
        return this.fee.toString();
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class TxPayment extends Tx {
    @JsonProperty("Account")
    public String from;

    @JsonProperty("Destination")
    public String to;

    public Long amount;

    @JsonGetter("Amount")
    public String getAmount() {
        return this.amount.toString();
    }

    @JsonCreator
    public TxPayment(@JsonProperty("Account") String from,
                     @JsonProperty("Destination") String to,
                     @JsonProperty("Amount") Double amount,
                     @JsonProperty("Fee") Double fee) {
        this.type = "Payment";
        this.fee = fee.longValue();
        this.from = from;
        this.to = to;
        this.amount = amount.longValue();
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class TxSetRegularKey extends Tx {
    @JsonProperty("Account")
    private String account;

    @JsonProperty("RegularKey")
    private String address;

    public TxSetRegularKey(String account, String address) {
        this.type = "SetRegularKey";
        this.account = account;
        this.address = address;
    }
}
