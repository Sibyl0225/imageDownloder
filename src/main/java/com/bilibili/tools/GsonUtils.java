package com.bilibili.tools;

import com.alibaba.fastjson.JSONException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Created by Xuzhiyuan on 2018/9/12.
 */
public class GsonUtils {

    public static Map<String, Object> toMap(JsonObject jsonobj) {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<Map.Entry<String, JsonElement>> iterator = jsonobj.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            } else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JsonArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JsonArray) {
                value = toList((JsonArray) value);
            } else if (value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            list.add(value);
        }
        return list;
    }

}
