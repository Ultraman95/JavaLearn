package com.nxquant.exchange.walletservice.dao;

import com.nxquant.exchange.walletservice.entity.AddressEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressMapper {
    public void save(AddressEntity addressEntity);

    public List<AddressEntity> findAll();

    public List<String> findAllByCoinid(int coinid);
}
