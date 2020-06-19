package org.elastos.hive.hivevault;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.vault.VaultOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientConnectTest {
    private static final String STORE_PATH = System.getProperty("user.dir");

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
            Client.Options options = new VaultOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .setDid("iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk")
                    .setPassword("adujejd")
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
