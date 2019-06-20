package org.elastos.hive.utils;

/**
 * Package: org.elastos.hive.utils
 * ClassName: UrlUtil
 * Created by ranwang on 2019/6/20.
 */
public class UrlUtil {
    public static String[] decodeHostAndPort(String requestUrl , String defaultHost , String defaultPort){
        String[] hostAndPort = new String[2];

        if (requestUrl.contains("//")){
            String[] urlInfo =requestUrl.split("//");
            requestUrl = urlInfo[1] ;
        }

        String[] hostInfo = requestUrl.split(":");

        hostAndPort[0] = defaultHost;
        hostAndPort[1] = defaultPort;

        if (hostInfo.length ==2 ){
            try {
                hostAndPort[0] = hostInfo[0];
                hostAndPort[1] = hostInfo[1];
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return hostAndPort ;
    }
}
