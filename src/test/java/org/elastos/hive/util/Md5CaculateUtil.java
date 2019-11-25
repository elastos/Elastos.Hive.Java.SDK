package org.elastos.hive.util;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertEquals;


public class Md5CaculateUtil {
    public static String getFileMD5(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }

    @Test
    public void testFileMd5(){
        String filePath = "/Users/wangran/Development/elastos/hive/Elastos.NET.Hive.AndroidDemo/hivesdk/src/resources/org/elastos/hive/test.txt";
        String expectMd5 = "973131af48aa1d25bf187dacaa5ca7c0";
        String actualMd5 = getFileMD5(filePath);
        assertEquals(expectMd5,actualMd5);
    }
}