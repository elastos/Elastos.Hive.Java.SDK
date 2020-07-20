package org.elastos.hive.vault;

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
    private static final String DID = "iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk";
    private static final String PWD = "adujejd";
    private static final String KEYNAME = "key2";
    private static final String NODEURL = "http://127.0.0.1:5000";

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
                    .setKeyName(KEYNAME)
                    .setStorePath(STORE_PATH)
                    .setDid(DID)
                    .setPassword(PWD)
                    .setNodeUrl(NODEURL)
                    .build();

            client = Client.createInstance(options);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }
}
