package org.elastos.hive.ipfs;

import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.elastos.hive.HiveConnectOptions;
import org.elastos.hive.HiveException;
import org.elastos.hive.IHiveConnect;
import org.elastos.hive.vendors.HiveRpcNode;
import org.elastos.hive.vendors.ipfs.IPFSConnectOptions;
import org.elastos.hive.vendors.ipfs.IPFSFile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IPFSFileRPCNullTest {
    private static IHiveConnect hiveConnect ;
    private static HiveClient hiveClient ;
    private static HiveRpcNode[] hiveRpcNodes = new HiveRpcNode[1];
    private static IPFSFile hiveFile ;

    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = HiveClient.createInstance(hiveOptions);

        hiveRpcNodes[0] = null ;

    }

    @AfterClass
    public static void tearDown(){
        hiveClient.disConnect(hiveConnect);
        hiveClient.close();
    }

    @Test
    public void testConnect() {
        HiveConnectOptions hiveConnectOptions = new IPFSConnectOptions(hiveRpcNodes);
        try {
            hiveConnect = hiveClient.connect(hiveConnectOptions);
        } catch (HiveException e) {
            e.printStackTrace();
            assertNotNull(e);
        }

        if (hiveConnect != null){
            hiveFile = hiveConnect.createHiveFile();
        }

        assertNull(hiveConnect);
        assertNull(hiveFile);
    }

}
