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

import org.elastos.hive.vendors.ipfs.IPFSConnect;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.elastos.hive.vendors.onedrive.OneDriveConnect;
import org.elastos.hive.vendors.onedrive.OneDriveConnectOptions;

public class HiveClient {
    private static HiveClient mInstance ;

    private HiveClient(HiveClientOptions hiveOptions){
    }

    public static HiveClient createInstance(HiveClientOptions hiveOptions) {
        if (mInstance == null){
            mInstance = new HiveClient(hiveOptions);
        }

        return mInstance;
    }

    public static HiveClient getInstance() {
        return mInstance;
    }

    public void close() {
        mInstance = null ;
    }

    public IHiveConnect connect(HiveConnectOptions hiveConnectOptions) throws HiveException {
        HiveConnectOptions.HiveBackendType backendType = hiveConnectOptions.getBackendType();
        IHiveConnect hiveConnect = null ;
        switch (backendType){
            case HiveBackendType_IPFS:
                hiveConnect = IPFSConnect.createInstance((IPFSConnectOptions)hiveConnectOptions);
                break;
            case HiveBackendType_OneDrive:
                hiveConnect = OneDriveConnect.createInstance((OneDriveConnectOptions)hiveConnectOptions);
                break;
            case HiveBackendType_ownCloud:
                break;
            case HiveDriveType_Butt:
                break;
            default:
                break;
        }
        hiveConnect.connect(hiveConnectOptions.getAuthenticator());
        return hiveConnect;
    }

    public int disConnect(IHiveConnect hiveConnect) {
        if (hiveConnect!=null) hiveConnect.disConnect();
        return 0;
    }
}
