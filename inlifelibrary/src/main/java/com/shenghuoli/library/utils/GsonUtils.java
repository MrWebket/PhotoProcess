package com.shenghuoli.library.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonUtils {

    /**
     * 解析数据-对象
     * 
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> T parse(String jsonString, Class<T> cls) {
        T t = null;

        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (JsonSyntaxException e) {
            LogUtil.error(cls, e.getMessage());
        }

        return t;
    }

    /**
     * 解析数据-数组
     * 
     * @param jsonString
     * @param type new TypeToken<List<T>>(){}.getType()
     * @return
     */
    public static <T> List<T> parseList(String jsonString, Type type) {
        List<T> list = new ArrayList<T>();
        
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            LogUtil.error(GsonUtils.class, e.getMessage());
        }

        return list;
    }

    /**
     * 将对象解析成json
     * 
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return new Gson().toJson(obj);
    }
    
}
