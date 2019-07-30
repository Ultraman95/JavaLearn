package com.nxquant.exchange.wallet.eos.messages;

import java.util.List;
import java.util.Map;

public class TransactionJSONArgs {
    public Map args;
    public List<String> required_scope;
    public List<String> required_auth;
}
