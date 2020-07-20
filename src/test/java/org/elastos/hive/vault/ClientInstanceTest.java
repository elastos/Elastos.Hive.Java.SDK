package org.elastos.hive.vault;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.vault.VaultOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientInstanceTest {

    private static final String DID = "iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk";
    private static final String PWD = "adujejd";
    private static final String NODEURL = "http://127.0.0.1:5000";

    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testCreateInstance() {
        try {
            Client.Options options = new VaultOptions.Builder()
                    .setStorePath(STORE_PATH)
                    .setDid(DID)
                    .setPassword(PWD)
                    .setNodeUrl(NODEURL)
                    .build();
            assertNotNull(options);
            assertNotNull(options.storePath());
            assertNotNull(options.authenticator());

            Client client = Client.createInstance(options);
            assertNotNull(client);
            assertFalse(client.isConnected());

        } catch (HiveException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateInstanceFailed1() {
        try {
            new VaultOptions.Builder()
                    .setDid(DID)
                    .build();
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed2() {
        try {
            new VaultOptions.Builder()
                    .setPassword(PWD)
                    .build();
        } catch (HiveException e) {
            assertTrue(true);
        }
    }


    @Test
    public void testCreateInstanceFailed3() {
        try {
            Client.Options options = new VaultOptions.Builder()
                    .setStorePath(STORE_PATH)
                    .build();
            assertNull(options);
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed5() {
        try {
            Authenticator authenticator = requestUrl -> {
                try {
                    Desktop.getDesktop().browse(new URI(requestUrl));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    fail();
                }
            };

            VaultOptions.Builder builder = new VaultOptions.Builder()
                    .setStorePath(STORE_PATH)
                    .setDid(DID)
                    .setPassword(PWD);

            Client.Options options = builder.build();
            assertNotNull(options);
            assertNotNull(options.storePath());
            assertNotNull(options.authenticator());

            VaultOptions opts = (VaultOptions) options;
            assertNotNull(opts.storePath());
            assertNotNull(opts.did());
            assertNotNull(opts.storePass());

            builder.build();
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
    }
}
