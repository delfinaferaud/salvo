package com.codeoftheweb.salvo.controller;

import java.util.HashMap;
import java.util.Map;

public class Util {

    public static Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
