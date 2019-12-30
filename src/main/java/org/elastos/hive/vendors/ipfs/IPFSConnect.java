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

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Authenticator;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.vendors.onedrive.OneDriveFile;

public class IPFSConnect implements IHiveConnect {
    private static IPFSConnect mIPFSConnectInstance ;
    private static IPFSRpc ipfsRpc ;

    private IPFSConnect(){
    }

    public static IHiveConnect createInstance(IPFSConnectOptions hiveConnectOptions){
        if (null == mIPFSConnectInstance){
            mIPFSConnectInstance = new IPFSConnect();
        }
        ipfsRpc = new IPFSRpc(hiveConnectOptions.getHiveRpcNodes());
        return mIPFSConnectInstance;
    }

    public static IHiveConnect getInstance(){
        return  mIPFSConnectInstance;
    }

    @Override
    public void connect(Authenticator authenticator) throws HiveException {
        ipfsRpc.checkReachable();
    }

    @Override
    public void disConnect() {
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public <T extends HiveFile> T createHiveFile(String filename) {
        return (T) new IPFSFile(ipfsRpc);
    }

    @Override
    public <T extends HiveFile> T createHiveFile() {
        return (T) new IPFSFile(ipfsRpc);
    }
}
