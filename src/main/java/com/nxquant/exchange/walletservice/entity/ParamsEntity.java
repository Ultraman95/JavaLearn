package com.nxquant.exchange.walletservice.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name = "t_params")
public class ParamsEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "sequence")
    @SequenceGenerator(name = "sequence",sequenceName = "seq_t_params")
    private int paramId;

    @Column(name = "paramcode",length = 50)
    @Length(min = 0,max = 50)
    private String paramCode;

    @Column(name = "paramdesc",length = 255)
    @Length(min = 0,max = 255)
    private String paramDesc;

    @Column(name = "paramvalue",length = 200)
    @Length(min = 0,max = 200)
    private String paramValue;


    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
