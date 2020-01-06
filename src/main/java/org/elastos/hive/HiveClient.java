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
    private OneDriveConnect oneDriveConnect ;
    private IPFSConnect ipfsConnect ;
    private HiveClientOptions options ;


    public HiveClient(HiveClientOptions hiveOptions){
        options = hiveOptions ;
    }

    public void close() {
    }

    public HiveConnect connect(HiveConnectOptions hiveConnectOptions){
        HiveConnectOptions.HiveBackendType backendType = hiveConnectOptions.getBackendType();
        HiveConnect hiveConnect = null ;
        switch (backendType){
            case HiveBackendType_IPFS:
                if (ipfsConnect == null)
                    ipfsConnect = new IPFSConnect((IPFSConnectOptions)hiveConnectOptions);
                hiveConnect = ipfsConnect;
                break;
            case HiveBackendType_OneDrive:
                if (oneDriveConnect == null)
                    oneDriveConnect = new OneDriveConnect((OneDriveConnectOptions)hiveConnectOptions , options.getStorePath());
                hiveConnect = oneDriveConnect;
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

    public int disConnect(HiveConnect hiveConnect) {
        if (hiveConnect!=null) hiveConnect.disConnect();
        return 0;
    }
}
