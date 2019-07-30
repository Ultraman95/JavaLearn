package com.nxquant.exchange.wallet.eos;

import com.nxquant.exchange.wallet.eos.exception.ChainException;
import com.nxquant.exchange.wallet.eos.messages.*;
import com.nxquant.exchange.wallet.eos.model.ProducerInfo;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Chain {
    ChainInfo getInfo() throws ChainException;

    BlockInfo getBlock(String blockNumOrID) throws ChainException;

    AccountInfo getAccount(String accountName) throws ChainException;

    ProducerSchedule getProducers() throws ChainException;

    Transaction createCreateAccountTransaction(String creator, String accountName, String ownerKey, String activeKey) throws ChainException;

	Transaction createSetProducersTransaction(String creator, Set<ProducerInfo> producers) throws ChainException;

	Code getCode(String accountName) throws ChainException;

    TableRows getTableRows(String contract,
                           String scope,
                           String table,
                           long limit,
                           boolean json) throws ChainException;

    TableRows getTableRows(String contract,
                           String scope,
                           String table,
                           String lowerBound,
                           String upperBound,
                           long limit,
                           boolean json) throws ChainException;

    TableRows getTableRows(String contract,
                           String scope,
                           String table,
                           Integer indexPosition,
                           String keyType,
                           String lowerBound,
                           String upperBound,
                           long limit,
                           boolean json) throws ChainException;

    TransactionBinArgs abiJsonToBin(String code,
                                    String action,
                                    Map args) throws ChainException;

    TransactionJSONArgs abiBinToJson(String code,
                                     String action,
                                     String binArgs) throws ChainException;

    RequiredKeys getRequiredKeys(Transaction transaction,
                                 List<String> availableKeys) throws ChainException;

    Transaction.Response pushTransaction(SignedTransaction transaction) throws ChainException;

    List<Transaction.Response> pushTransactions(List<SignedTransaction> signedTransactions) throws ChainException;

    Transaction createRawTransaction(String account,
                                     String name,
                                     Map args,
                                     List<String> scopes,
                                     List<Transaction.Authorization> authorizations,
                                     Date expirationDate) throws ChainException;

    Transaction createRawTransaction(Action action, Date expirationDate)
            throws ChainException;

    Transaction createRawTransaction(List<Action> actions, Date expirationDate)
                    throws ChainException;

    Transaction createSetContractTransaction(String account, InputStream abi, InputStream wasm) throws ChainException;

    String packTransaction(SignedTransaction transaction) throws ChainException;
}
