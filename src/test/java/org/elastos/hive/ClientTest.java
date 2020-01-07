package org.elastos.hive;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClientTest {
    private static HiveClient hiveClient ;
    private static final String STORE_PATH = System.getProperty("user.dir");

    @Test
    public void testGetInstance() {
        assertNotNull(hiveClient);
    }

    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions.Builder().storePath(STORE_PATH).build();
        hiveClient = new HiveClient(hiveOptions);
    }

    @AfterClass
    public static void tearDown() {
        hiveClient.close();
    }
}
