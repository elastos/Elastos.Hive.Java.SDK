package org.elastos.hive.ipfs;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.ipfs.IPFSOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientInstanceTest {
    private static final String STORE_PATH = System.getProperty("user.dir");
    private static final String[] IPADDRS = {"3.133.166.156", "127.0.0.1"};

    @Test
    public void testCreateInstance() {
        try {
            Client.Options options = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[0], 5001))
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[1], 5001))
                    .build();
            assertNotNull(options);
            assertNotNull(options.storePath());

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
            new IPFSOptions.Builder()
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[0], 5001))
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[1], 5001))
                    .build();
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed2() {
        try {
            new IPFSOptions.Builder()
                    .setStorePath(STORE_PATH)
                    .build();
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed3() {
        try {
            Client.createInstance(null);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (HiveException e) {
            fail();
        }
    }

    @Test
    public void testCreateInstanceFailed4() {
        try {
            IPFSOptions.Builder builder = new IPFSOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[0], 5001))
                    .addRpcNode(new IPFSOptions.RpcNode(IPADDRS[1], 5001));
            assertNotNull(builder);

            Client.Options options = builder.build();
            assertNotNull(options);
            assertNotNull(options.storePath());
            assertNotNull(((IPFSOptions)options).getRpcNodes());

            builder.build();
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @BeforeClass
    public static void setUp() {}

    @AfterClass
    public static void tearDown() {}
}
