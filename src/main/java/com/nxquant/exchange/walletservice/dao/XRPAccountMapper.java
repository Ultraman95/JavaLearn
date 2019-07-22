package com.nxquant.exchange.walletservice.dao;

import com.nxquant.exchange.walletservice.entity.XRPAccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XRPAccountMapper {
    public void save(XRPAccountEntity xrpAccountEntity);
}
