package org.elastos.hive.utils;

/**
 * Package: org.elastos.hive.utils
 * ClassName: LogUtil
 * Created by ranwang on 2019/6/18.
 */
public class LogUtil{
    public static boolean debug = true ;
    private static final String TAG = "Hive Debug";
    private static final String ERROR_TAG = "Hive Error";

    public static void d(String msg) {
        if (debug){
            System.out.println(TAG+" ==> "+msg);
        }
    }

    public static void d(String tag, String msg) {
        if (debug){
            System.out.println(tag+" ==> "+msg);
        }
    }

    public static void e(String msg) {
        System.out.println(ERROR_TAG+" ==> "+msg);
    }

    public static void e(String tag, String msg) {
        System.out.println(tag+" ==> "+msg);
    }
}
