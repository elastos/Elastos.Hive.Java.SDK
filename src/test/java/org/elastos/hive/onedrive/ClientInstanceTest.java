package org.elastos.hive.onedrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.onedrive.OneDriveOptions;
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
    private static final String CLIENTID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";
    private static final String REDIRECTURL = "http://localhost:12345";
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testCreateInstance() {
        try {
            Authenticator authenticator = requestUrl -> {
                try {
                    Desktop.getDesktop().browse(new URI(requestUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            };

            Client.Options options = new OneDriveOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .setClientId(CLIENTID)
                    .setRedirectUrl(REDIRECTURL)
                    .setAuthenticator(authenticator)
                    .build();
            assertNotNull(options);


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
            Client.Options options = new OneDriveOptions
                    .Builder()
                    .setClientId(CLIENTID)
                    .build();
            assertNull(options);
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed2() {
        try {
            Client.Options options = new OneDriveOptions
                    .Builder()
                    .setRedirectUrl(REDIRECTURL)
                    .build();
            assertNull(options);
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed3() {
        try {
            Client.Options options = new OneDriveOptions
                    .Builder()
                    .setAuthenticator(requestUrl -> {
                        try {
                            Desktop.getDesktop().browse(new URI(requestUrl));
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail();
                        }
                    })
                    .build();
            assertNull(options);
        } catch (HiveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCreateInstanceFailed4() {
        try {
            Client.Options options = new OneDriveOptions
                    .Builder()
                    .setStorePath(STORE_PATH)
                    .build();
            assertNull(options);
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
