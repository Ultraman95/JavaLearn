package com.nxquant.exchange.walletservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonUtil {
    public static final ObjectMapper jackson = new ObjectMapper();

    public static final Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(new TypeToken<Map<String, Object>>() {
    }.getType(), (JsonDeserializer<Map<String, Object>>) (json, typeOfT, context) -> {
        Map<String, Object> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }).create();
}
