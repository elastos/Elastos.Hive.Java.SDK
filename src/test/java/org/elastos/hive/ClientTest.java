package org.elastos.hive;

import org.elastos.hive.HiveClient;
import org.elastos.hive.HiveClientOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ClientTest {
    private static HiveClient hiveClient ;

    @Test
    public void testGetInstance() {
        assertNotNull(HiveClient.getInstance());
    }

    @BeforeClass
    public static void setUp() throws Exception {
        HiveClientOptions hiveOptions = new HiveClientOptions();
        hiveClient = HiveClient.createInstance(hiveOptions);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hiveClient.close();
    }
}
