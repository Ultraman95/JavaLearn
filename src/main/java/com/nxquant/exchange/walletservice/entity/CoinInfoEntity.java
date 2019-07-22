package com.nxquant.exchange.walletservice.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "t_coininfo")
public class CoinInfoEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "sequence")
    @SequenceGenerator(name = "sequence",sequenceName = "seq_t_coininfo")
    private int id;

    @Column(name = "coinname",length = 10)
    @Length(min = 0,max = 10)
    private String coinname;

    @Column(name = "coinprecision",length = 11)
    private int coinprecision;

    @Column(name = "confirmations",length = 11)
    private int confirmations;

    @Column(name = "maxlittlewithdrawamount",length = 11)
    private BigDecimal maxlittlewithdrawamount;

    @Column(name = "contractasset",length = 11)
    private boolean contractasset;

    @Column(name = "contractmainchainasset",length = 10)
    @Length(min = 0,max = 10)
    private String contractmainchainasset;

    @Column(name = "contractaddress",length = 150)
    @Length(min = 0,max = 150)
    private String contractaddress;

    @Column(name = "coinid",length = 11)
    private int coinid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoinname() {
        return coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public int getCoinprecision() {
        return coinprecision;
    }

    public void setCoinprecision(int coinprecision) {
        this.coinprecision = coinprecision;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public BigDecimal getMaxlittlewithdrawamount() {
        return maxlittlewithdrawamount;
    }

    public void setMaxlittlewithdrawamount(BigDecimal maxlittlewithdrawamount) {
        this.maxlittlewithdrawamount = maxlittlewithdrawamount;
    }

    public boolean isContractasset() {
        return contractasset;
    }

    public void setContractasset(boolean contractasset) {
        this.contractasset = contractasset;
    }

    public String getContractmainchainasset() {
        return contractmainchainasset;
    }

    public void setContractmainchainasset(String contractmainchainasset) {
        this.contractmainchainasset = contractmainchainasset;
    }

    public String getContractaddress() {
        return contractaddress;
    }

    public void setContractaddress(String contractaddress) {
        this.contractaddress = contractaddress;
    }

    public int getCoinid() {
        return coinid;
    }

    public void setCoinid(int coinid) {
        this.coinid = coinid;
    }
}
