package org.elastos.hive.utils;

/**
 * Package: org.elastos.hive.utils
 * ClassName: CheckTextUtil
 * Created by ranwang on 2019/6/18.
 */
public final class CheckTextUtil {
    public static boolean isEmpty(final String text){
        if (text == null ||text.length() == 0) {
            return true;
        }
        return false;
    }
}
