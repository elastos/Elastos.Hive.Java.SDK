package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.util.concurrent.ExecutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IpfsClientTest {
	private static Client client;

	@Test public void testGetInstance() {
		assertNotNull(Client.getInstance(DriveType.hiveIpfs));
	}

	@Test public void testGetInfo() {
		try {
			Client.Info info = client.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.getUserId());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}

	@Test public void testGetDefaultDrive() {
		try {
			Drive drive = client.getDefaultDrive().get();
			assertNotNull(drive);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetDefaultDrive failed");
		}
	}

	@BeforeClass
	static public void setUp() throws Exception {
		client = IpfsTestBase.login();
		assertNotNull(client);
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
