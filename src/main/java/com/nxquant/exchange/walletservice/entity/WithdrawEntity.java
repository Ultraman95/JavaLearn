package com.nxquant.exchange.walletservice.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "t_withdraw")
public class WithdrawEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "sequence")
    @SequenceGenerator(name = "sequence",sequenceName = "seq_t_withdraw")
    private int withdrawId;

    @Column(name = "orderid",length = 30)
    @Length(min = 0,max = 30)
    private String orderid;

    @Column(name = "toaddr",length = 100)
    @Length(min = 0,max = 100)
    private String toaddr;

    @Column(name = "value",precision = 19,scale = 10)
    @Digits(integer = 10,fraction = 9)
    @Min(0)
    private BigDecimal value;

    @Column(name = "coinid",length = 2)
    private int coinid;

    @Column(name = "passwd",length = 50)
    @Length(min = 0,max = 50)
    private String passwd;

    @Column(name = "requesttime")
    private Date requestTime;

    @Column(name = "processtime")
    private Date processTime;

    @Column(name = "status")
    private int status;

    @Column(name = "auditing")
    private int Auditing;

    @Column(name = "auditor",length = 30)
    @Length(min = 0,max = 30)
    private String Auditor;

    @Column(name = "txid", length = 100)
    @Length(min = 0,max = 100)
    private String txId;

    @Column(name = "startheight")
    private long startHeight;

    @Column(name = "txheight")
    private long txHeight;

    @Column(name = "remark", length = 500)
    @Length(min = 0,max = 500)
    private String remark;

    public int getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(int withdrawId) {
        this.withdrawId = withdrawId;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getToaddr() {
        return toaddr;
    }

    public void setToaddr(String toaddr) {
        this.toaddr = toaddr;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getCoinid() {
        return coinid;
    }

    public void setCoinid(int coinid) {
        this.coinid = coinid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuditing() {
        return Auditing;
    }

    public void setAuditing(int auditing) {
        Auditing = auditing;
    }

    public String getAuditor() {
        return Auditor;
    }

    public void setAuditor(String auditor) {
        Auditor = auditor;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public long getStartHeight() {
        return startHeight;
    }

    public void setStartHeight(long startHeight) {
        this.startHeight = startHeight;
    }

    public long getTxHeight() {
        return txHeight;
    }

    public void setTxHeight(long txHeight) {
        this.txHeight = txHeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
