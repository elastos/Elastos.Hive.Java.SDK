package org.elastos.hive.vendor.hivevault;

import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AuthInfoStoreImpl implements Persistent {
    private String storePath;

    AuthInfoStoreImpl(String storePath) {
        this.storePath = String.format("%s/%s", storePath, HiveVaultConstance.CONFIG);
    }

    @Override
    public JSONObject parseFrom() throws HiveException {
        FileReader reader = null;
        try {
            initialize();
            reader = new FileReader(storePath);
            char[] buf = new char[128];
            int len;
            StringBuilder content = new StringBuilder();
            while ((len = reader.read(buf)) != -1) {
                content.append(new String(buf, 0, len));
            }

            if (content.length() > 0) {
                return new JSONObject(content.toString());
            }
        } catch (Exception e) {
            throw new HiveException(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new JSONObject();
    }

    @Override
    public void upateContent(JSONObject conetnt) throws HiveException {
        FileWriter fileWriter = null;
        try {
            initialize();
            fileWriter = new FileWriter(storePath);
            fileWriter.write(conetnt.toString());
        } catch (Exception e) {
            throw new HiveException(e.getMessage());
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialize() throws IOException {
        File config = new File(storePath);
        if (!config.exists())
            config.createNewFile();
    }
}
