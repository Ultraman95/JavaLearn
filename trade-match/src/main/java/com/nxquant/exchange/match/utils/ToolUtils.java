package com.nxquant.exchange.match.utils;

import com.google.gson.Gson;

import com.nxquant.exchange.match.dto.*;
import java.util.concurrent.atomic.AtomicLong;


public class ToolUtils {
    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    public static AtomicLong incLong = new AtomicLong(0);
}
