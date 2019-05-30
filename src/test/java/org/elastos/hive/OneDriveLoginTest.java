package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

class OneDriveLoginTest {
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
