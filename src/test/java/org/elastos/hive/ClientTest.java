package org.elastos.hive;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClientTest {
    private static Client hiveClient ;
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testGetInstance() {
        assertNotNull(hiveClient);
    }

    @BeforeClass
    public static void setUp() {
        ClientOptions hiveOptions = new ClientOptions.Builder().setStorePath(STORE_PATH).build();
        hiveClient = new Client(hiveOptions);
    }

    @AfterClass
    public static void tearDown() {
    }
}
