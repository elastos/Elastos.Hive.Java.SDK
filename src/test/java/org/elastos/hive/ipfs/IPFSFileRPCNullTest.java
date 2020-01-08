package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.ClientOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.ConnectOptions;
import org.elastos.hive.vendors.ipfs.IPFSRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IPFSFileRPCNullTest {
    private static Client client ;
    private static HiveConnect connect ;
    private static IPFSRpcNode[] rpcNodes = new IPFSRpcNode[1];
    private static final String STORE_PATH = System.getProperty("user.dir");

    @BeforeClass
    public static void setUp() {
        ClientOptions options = new ClientOptions
                .Builder()
                .setStorePath(STORE_PATH)
                .build();
        client = new Client(options);

        rpcNodes[0] = null ;

    }

    @AfterClass
    public static void tearDown(){
        client.disConnect(connect);
    }

    @Test
    public void testConnect() {
        ConnectOptions options = new IPFSConnectOptions
                .Builder()
                .setRpcNodes(rpcNodes)
                .build();
        connect = client.connect(options);
        // TODO: assert.
    }
}
