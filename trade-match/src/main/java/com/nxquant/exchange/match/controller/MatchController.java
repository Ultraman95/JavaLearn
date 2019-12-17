package com.nxquant.exchange.match.controller;

import com.nxquant.exchange.match.core.MainWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shilf
 * Match接受HTTP消息接口
 */
@RestController
public class MatchController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MainWorker mainWorker;

    @GetMapping(value = "/stop")
    public String stopMainWork(HttpServletRequest request) {
        String rspStr;
        if(mainWorker!=null){
            mainWorker.setMainWorkerStop(true);
            rspStr = "ok";
        }else {
            rspStr = "error";
        }
        return rspStr;
    }


    @GetMapping(value = "/export")
    public String exportOrderBook(@RequestParam(value = "symbol") String symbol,
                                  HttpServletRequest request) {
        return "";

    }


}
