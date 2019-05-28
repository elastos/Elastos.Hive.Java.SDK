package org.elastos.hive;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.junit.Before;

public class OneDriveDriveTest {
	private static Drive drive;
	private static boolean isLogin = false;

	@Before
	public void setUp() throws Exception {
		if (!isLogin) {
			testLogin();
		}
	}

	void testLogin() {
		class TestAuthenticator implements Authenticator {
			@Override
			public void requestAuthentication(String requestUrl) {
				try {
					Desktop.getDesktop().browse(new URI(requestUrl));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			OAuthEntry entry = new OAuthEntry(OneDriveTestBase.APPID,
					OneDriveTestBase.SCOPE, OneDriveTestBase.REDIRECTURL);
			OneDriveParameter parameter = new OneDriveParameter(entry);

			Client client = Client.createInstance(parameter);

			TestAuthenticator authenticator = new TestAuthenticator();
			client.login(authenticator);

			drive = client.getDefaultDrive().get();
			assertTrue(drive != null);

			isLogin = true;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Test login errror", false);
		}
    }

	void checkLogin() {
		if (!isLogin) {
			assertTrue("Please login first", false);
		}
	}
}
