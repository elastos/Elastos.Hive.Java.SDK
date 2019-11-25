package org.elastos.hive.ipfs;

import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.vendors.HiveRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IPFSConnectTest {

    private static IHiveConnect hiveConnect ;
    private static HiveClient hiveClient ;
    private static HiveRpcNode[] hiveRpcNodes = new HiveRpcNode[5];

    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = HiveClient.createInstance(hiveOptions);
        hiveRpcNodes[0] = new HiveRpcNode("3.133.166.156",5001);
        hiveRpcNodes[1] = new HiveRpcNode("13.59.79.222",5001);
        hiveRpcNodes[2] = new HiveRpcNode("3.133.71.168",5001);
        hiveRpcNodes[3] = new HiveRpcNode("107.191.44.124",5001);
        hiveRpcNodes[4] = new HiveRpcNode("127.0.0.1",5001);
    }


    @Test
    public void testConnect() throws HiveException {
        HiveConnectOptions hiveConnectOptions = new IPFSConnectOptions(hiveRpcNodes);
        hiveConnect = hiveClient.connect(hiveConnectOptions);
        assertNotNull(hiveConnect);
    }

    @AfterClass
    public static void tearDown(){
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }
}
