package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.ipfs.IPFSOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ClientConnectTest {
    private static final String STORE_PATH = System.getProperty("user.dir");
    private static final String[] IPADDRS = {"3.133.166.156", "127.0.0.1"};

    private static Client client;

    @Test
    public void testConnect() {
        try {
            assertFalse(client.isConnected());

            client.connect();
            assertTrue(client.isConnected());

            client.disconnect();
            assertFalse(client.isConnected());

        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[0], 5001))
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[1], 5001))
                    .build();

            client = Client.createInstance(options);
        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }
}
