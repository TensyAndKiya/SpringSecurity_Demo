package com.clei.util;

import java.util.UUID;

/**
 * id util
 *
 * @author KIyA
 * @date 2020-04-17
 */
public class IdUtil {

    public static String ID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
