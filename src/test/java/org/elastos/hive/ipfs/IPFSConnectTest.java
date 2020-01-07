package org.elastos.hive.ipfs;

import org.elastos.hive.HiveClient;
import org.elastos.hive.ClientOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.ConnectOptions;
import org.elastos.hive.vendors.ipfs.IPFSRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IPFSConnectTest {

    private static HiveConnect hiveConnect ;
    private static HiveClient hiveClient ;
    private static IPFSRpcNode[] hiveRpcNodes = new IPFSRpcNode[5];
    private static final String STORE_PATH = System.getProperty("user.dir");

    @BeforeClass
    public static void setUp() {
        ClientOptions hiveOptions = new ClientOptions.Builder().setStorePath(STORE_PATH).build();
        hiveClient = new HiveClient(hiveOptions);
        hiveRpcNodes[0] = new IPFSRpcNode("3.133.166.156",5001);
        hiveRpcNodes[1] = new IPFSRpcNode("13.59.79.222",5001);
        hiveRpcNodes[2] = new IPFSRpcNode("3.133.71.168",5001);
        hiveRpcNodes[3] = new IPFSRpcNode("107.191.44.124",5001);
        hiveRpcNodes[4] = new IPFSRpcNode("127.0.0.1",5001);
    }


    @Test
    public void testConnect(){
        ConnectOptions hiveConnectOptions = new IPFSConnectOptions.Builder().setRpcNodes(hiveRpcNodes).build();
        hiveConnect = hiveClient.connect(hiveConnectOptions);
        assertNotNull(hiveConnect);
    }

    @AfterClass
    public static void tearDown(){
        hiveClient.disConnect(hiveConnect);
    }
}
