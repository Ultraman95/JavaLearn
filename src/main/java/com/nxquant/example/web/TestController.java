package com.nxquant.example.web;

import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = "/test")
    public String getHistoryKline(@RequestParam(value = "symbol") String symbol,
                                  HttpServletRequest request) {
        if(StringUtils.isEmpty(symbol)){
            return symbol;
        }
        return "error";
    }
}
