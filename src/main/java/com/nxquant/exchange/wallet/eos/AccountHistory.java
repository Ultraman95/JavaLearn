package com.nxquant.exchange.wallet.eos;

public interface AccountHistory {
    void getTransaction(String transactionID);

    void getTransactions(String accountName);

    void getKeyAccounts(String publicKey);

    void getControlledAccounts(String accountName);
}
