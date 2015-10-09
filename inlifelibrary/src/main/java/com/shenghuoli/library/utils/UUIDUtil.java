
package com.shenghuoli.library.utils;

import java.util.UUID;

/**
 * UUID产生工具类
 * @author dbzhuang
 *
 */
public class UUIDUtil {
    private UUIDUtil() {
    }
    
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
