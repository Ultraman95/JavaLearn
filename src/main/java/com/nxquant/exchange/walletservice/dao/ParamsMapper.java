package com.nxquant.exchange.walletservice.dao;

import com.nxquant.exchange.walletservice.entity.ParamsEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParamsMapper {

    public ParamsEntity findParamValueByCode(String paramCode);

    public int addParams(ParamsEntity paramsEntity);

    public void UpdateParamByCode(ParamsEntity paramsEntity);
}
