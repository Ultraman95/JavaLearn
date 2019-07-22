package com.nxquant.exchange.walletservice.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "t_recharge")
public class RechargeEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "sequence")
    @SequenceGenerator(name = "sequence",sequenceName = "seq_t_recharge")
    private int rechargeId;

    @Column(name = "coinid",length = 2)
    private int coinId;

    @Column(name = "txid",length = 200)
    @Length(min = 0,max = 200)
    private String txId;

    @Column(name = "fromaddr",length = 150)
    @Length(min = 0,max = 150)
    private String fromAddr;

    @Column(name = "toaddr",length = 150)
    @Length(min = 0,max = 150)
    private String toAddr;

    @Column(name = "value",precision = 19,scale = 10)
    @Digits(integer = 10,fraction = 9)
    @Min(0)
    private BigDecimal value;

    @Column(name = "fee",precision = 19,scale = 10)
    @Digits(integer = 10,fraction = 9)
    @Min(0)
    private BigDecimal fee;

    @Column(name = "receivetime")
    private Date receiveTime;

    @Column(name = "status",length = 1)
    private int status;

    @Column(name = "remark",length = 500)
    @Length(min = 0,max = 500)
    private String remark;

    public int getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(int rechargeId) {
        this.rechargeId = rechargeId;
    }

    public int getCoinId() {
        return coinId;
    }

    public void setCoinId(int coinId) {
        this.coinId = coinId;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

