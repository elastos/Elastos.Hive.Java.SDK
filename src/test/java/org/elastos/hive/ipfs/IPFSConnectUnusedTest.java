package org.elastos.hive.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.result.Void;
import org.elastos.hive.vendors.ipfs.IPFSRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IPFSConnectUnusedTest {

    private static HiveConnect hiveConnect ;
    private static HiveClient hiveClient ;
    private static IPFSRpcNode[] hiveRpcNodes = new IPFSRpcNode[1];
    private static final String STORE_PATH = System.getProperty("user.dir");

    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions.Builder().setStorePath(STORE_PATH).build();
        hiveClient = new HiveClient(hiveOptions);
        hiveRpcNodes[0] = new IPFSRpcNode("127.0.0.2",5001);
    }


    @Test
    public void testConnect(){
        HiveConnectOptions hiveConnectOptions = new IPFSConnectOptions.Builder().ipfsRPCNodes(hiveRpcNodes).build();
        hiveConnect = hiveClient.connect(hiveConnectOptions);
    }

    @AfterClass
    public static void tearDown() {
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }
}
