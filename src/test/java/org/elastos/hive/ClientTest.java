package org.elastos.hive;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClientTest {
    private static HiveClient hiveClient ;

    @Test
    public void testGetInstance() {
        assertNotNull(hiveClient);
    }

    @BeforeClass
    public static void setUp() {
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = new HiveClient(hiveOptions);
    }

    @AfterClass
    public static void tearDown() {
        hiveClient.close();
    }
}
