package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OneDriveClientTest {
	private static Client client;
	@Test public void testGetInstance() {
		assertNotNull(Client.getInstance(DriveType.oneDrive));
	}

	@Test public void testGetInfo() {
		try {
			ClientInfo info = client.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.getUserId());
			assertNotNull(info.getDisplayName());
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
			fail("getInfo failed");
		}
	}

	@BeforeClass
	static public void setUp() throws Exception {
		client = OneDriveTestBase.login();
		assertNotNull(client);
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
