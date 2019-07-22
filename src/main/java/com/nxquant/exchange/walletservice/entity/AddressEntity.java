package com.nxquant.exchange.walletservice.entity;

import org.hibernate.validator.constraints.Length;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_address")
public class AddressEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "sequence")
    @SequenceGenerator(name = "sequence",sequenceName = "seq_t_address")
    private int id;

    @Column(name = "coinid")
    private  int coinid;

    @Column(name = "address", length = 100)
    @Length(min = 0,max = 100)
    private String address;

    @Column(name = "createdate")
    private Date createdate;

    @Column(name = "isavailable")
    private boolean isavailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoinid() {
        return coinid;
    }

    public void setCoinid(int coinid) {
        this.coinid = coinid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public boolean isIsavailable() {
        return isavailable;
    }

    public void setIsavailable(boolean isavailable) {
        this.isavailable = isavailable;
    }
}
