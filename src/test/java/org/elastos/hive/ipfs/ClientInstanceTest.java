package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.ipfs.IPFSOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientInstanceTest {
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testCreateInstance() {
        try {
            IPFSOptions.RpcNode node = new IPFSOptions.RpcNode("3.133.166.156", 5001);
            IPFSOptions.RpcNode node2 = new IPFSOptions.RpcNode("127.0.0.1", 5001);

            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(node)
                    .addRpcNode(node2)
                    .build();
            assertNotNull(options);

            Client client = Client.createInstance(options);
            assertNotNull(client);

            client.connect();
            assertTrue(client.isConnected());

        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateInstanceFailed1() {
        try {
            IPFSOptions.RpcNode node = new IPFSOptions.RpcNode("3.133.166.156", 5001);
            IPFSOptions.RpcNode node2 = new IPFSOptions.RpcNode("127.0.0.1", 5001);

            Client.Options options = new IPFSOptions
                    .Builder()
                    .addRpcNode(node)
                    .addRpcNode(node2)
                    .build();
            assertNull(options);
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed2() {
        try {
            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .build();
            assertNull(options);
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed3() {
        try {
            Client client = Client.createInstance(null);
            assertNull(client);

        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (HiveException e) {
            fail();
        }
    }

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
    }
}
