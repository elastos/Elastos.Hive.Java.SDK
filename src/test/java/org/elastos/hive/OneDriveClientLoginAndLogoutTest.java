package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

public class OneDriveClientLoginAndLogoutTest {
	@Test public void testLoginAndLogout() {
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
