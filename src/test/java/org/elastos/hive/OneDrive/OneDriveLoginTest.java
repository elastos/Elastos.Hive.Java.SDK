package org.elastos.hive.OneDrive;

import org.elastos.hive.Client;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class OneDriveLoginTest {
	@Test public void testLogin() {
		try {
			Client client = OneDriveTestBase.login();
			assertNotNull(client);
			client.logout();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Login or logout failed");
		}
    }
}
