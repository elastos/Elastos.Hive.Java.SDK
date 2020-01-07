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
import org.elastos.hive.vendors.onedrive.OneDriveConnect;

import java.util.HashMap;

import static org.elastos.hive.ConnectType.IPFS;
import static org.elastos.hive.ConnectType.OneDrive;

public class HiveClient {
    private HashMap<ConnectType, HiveConnect> connectMap;
    private ClientOptions options ;

    public HiveClient(ClientOptions options){
        this.options = options;
        this.connectMap = new HashMap<>();
    }

    synchronized public HiveConnect connect(ConnectOptions connectOptions) {
        ConnectType type = connectOptions.getConnectType();
        HiveConnect connector = null ;

        if (connectMap.containsKey(type))
            return connectMap.get(type);

        switch (type){
            case IPFS:
                connector = new IPFSConnect(connectOptions);
                connectMap.put(IPFS, connector);
                break;

            case OneDrive:
                connector = new OneDriveConnect(connectOptions, options.getStorePath());
                connectMap.put(OneDrive, connector);
                break;

            case OwnCloud:
            default:
                return null;
        }

        connector.connect(connectOptions.getAuthenticator()); // TODO: if error.
        return connector;
    }

    public HiveConnect getConnect(ConnectType type) {
        if (!connectMap.containsKey(type))
            return null;

        return connectMap.get(type);
    }

    // TODO: really need this one.
    public int disConnect(HiveConnect hiveConnect) {
        if (hiveConnect!=null) hiveConnect.disConnect();
        return 0;
    }
}
