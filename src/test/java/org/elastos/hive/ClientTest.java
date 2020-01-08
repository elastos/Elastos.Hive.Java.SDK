package org.elastos.hive;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClientTest {
    private static Client client ;
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testGetInstance() {
        assertNotNull(client);
    }

    @BeforeClass
    public static void setUp() {
        ClientOptions options = new ClientOptions
                .Builder()
                .setStorePath(STORE_PATH)
                .build();
        client = new Client(options);
    }

    @AfterClass
    public static void tearDown() {
    }
}
