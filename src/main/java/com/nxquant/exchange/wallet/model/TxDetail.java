package com.nxquant.exchange.wallet.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author jimmie
 * @date 2018/8/30 11:30
 */
public class TxDetail {
	private String toAddress;
	private BigDecimal txAmount;
	private BigDecimal txFee;
	private int confirmations;
	private String blockHash;
	private String txId;
	private String txReceiveTime;
	private long blockHeight;
	private String category;

	//ETH智能合约专用(ERC20)
	private String contractAddress;
	private BigInteger contractValue;
	private boolean isContractTx;

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public BigDecimal getTxAmount() {
		return txAmount;
	}

	public void setTxAmount(BigDecimal txAmount) {
		this.txAmount = txAmount;
	}

	public BigDecimal getTxFee() {
		return txFee;
	}

	public void setTxFee(BigDecimal txFee) {
		this.txFee = txFee;
	}

	public int getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(int confirmations) {
		this.confirmations = confirmations;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTxReceiveTime() {
		return txReceiveTime;
	}

	public void setTxReceiveTime(String txReceiveTime) {
		this.txReceiveTime = txReceiveTime;
	}

	public long getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(long blockHeight) {
		this.blockHeight = blockHeight;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public BigInteger getContractValue() {
		return contractValue;
	}

	public void setContractValue(BigInteger contractValue) {
		this.contractValue = contractValue;
	}

	public boolean isContractTx() {
		return isContractTx;
	}

	public void setContractTx(boolean contractTx) {
		isContractTx = contractTx;
	}
}
