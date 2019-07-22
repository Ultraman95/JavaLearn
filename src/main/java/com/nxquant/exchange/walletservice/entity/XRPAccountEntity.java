package com.nxquant.exchange.walletservice.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name = "t_xrpaccount")
public class XRPAccountEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "sequence")
    @SequenceGenerator(name = "sequence",sequenceName = "seq_t_xrpaccount")
    private int id;

    @Column(name = "accountid", length = 200)
    @Length(min = 0,max = 200)
    private String accountId;

    @Column(name = "masterseed", length = 150)
    @Length(min = 0,max = 150)
    private String masterSeed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMasterSeed() {
        return masterSeed;
    }

    public void setMasterSeed(String masterSeed) {
        this.masterSeed = masterSeed;
    }
}
