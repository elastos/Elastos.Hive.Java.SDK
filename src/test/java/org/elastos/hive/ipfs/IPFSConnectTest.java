package org.elastos.hive.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.result.Void;
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

    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = new HiveClient(hiveOptions);
        hiveRpcNodes[0] = new IPFSRpcNode("3.133.166.156",5001);
        hiveRpcNodes[1] = new IPFSRpcNode("13.59.79.222",5001);
        hiveRpcNodes[2] = new IPFSRpcNode("3.133.71.168",5001);
        hiveRpcNodes[3] = new IPFSRpcNode("107.191.44.124",5001);
        hiveRpcNodes[4] = new IPFSRpcNode("127.0.0.1",5001);
    }


    @Test
    public void testConnect(){
        HiveConnectOptions hiveConnectOptions = new IPFSConnectOptions(hiveRpcNodes);
        hiveConnect = hiveClient.connect(hiveConnectOptions, new Callback<Void>() {
            @Override
            public void onError(HiveException e) {
            }

            @Override
            public void onSuccess(Void body) {
                assertNotNull(body);
            }
        });
        assertNotNull(hiveConnect);
    }

    @AfterClass
    public static void tearDown(){
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }
}
