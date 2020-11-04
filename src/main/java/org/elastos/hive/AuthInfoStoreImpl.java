/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.CryptoUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AuthInfoStoreImpl implements Persistent {
    private String ownerDid;
    private String provider;
    private String storePath;

    public AuthInfoStoreImpl(String storePath) {
        this.storePath = storePath;
    }

    @Override
    public JSONObject parseFrom(String ownerDid, String provider, String userDid) throws HiveException {
        FileReader reader = null;
        try {
            initialize();
            reader = new FileReader(this.configPath);
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
    public void upateContent(JSONObject conetnt, String ownerDid, String provider, String userDid) throws HiveException {
        FileWriter fileWriter = null;
        try {
            initialize();
            fileWriter = new FileWriter(configPath);
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

    private String configPath;
    private void initialize() throws IOException {
        String tokenPath = String.format("%s/%s", storePath,"token");
        File rootDir = new File(tokenPath);
        if (!rootDir.exists())
            rootDir.mkdirs();
        String fileName = CryptoUtil.getSHA256(this.ownerDid+this.provider);
         this.configPath = String.format("%s/%s", tokenPath, fileName);
        File config = new File(configPath);
        if (!config.exists())
            config.createNewFile();
    }
}
