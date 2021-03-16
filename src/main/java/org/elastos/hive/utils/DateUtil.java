package org.elastos.hive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getCurrentEpochTimeStamp(long timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            String date = sdf.format(new Date(timeStamp));
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
