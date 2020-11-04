package org.elastos.hive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class Utils {

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if(file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static byte[] readImage(String path) {
        try {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(path);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                return buffer;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void cacheTextFile(Reader reader, String path) {
        FileWriter fileWriter = null;
        try {
            File file = new File(path);
            if(!file.exists()) file.mkdirs();
            fileWriter = new FileWriter(file);
            char[] buffer = new char[1];
            while (reader.read(buffer) != -1) {
                fileWriter.write(buffer);
            }
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cacheBinFile(InputStream inputStream, String storePath) {
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=inputStream.read(buffer)) != -1 ) {
                outStream.write(buffer, 0, len);
            }

            byte[] data = outStream.toByteArray();
            File file = new File(storePath);
            if(!file.exists()) file.mkdirs();
            FileOutputStream fileOutStream = new FileOutputStream(file);
            fileOutStream .write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
