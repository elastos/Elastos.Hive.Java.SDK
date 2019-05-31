package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.Desktop;
import java.net.URI;

import org.elastos.hive.vendors.onedrive.OneDriveParameter;

public class OneDriveTestBase {
	static public final String APPID = "f0f8fdc1-294e-4d5c-b3d8-774147075480";
	static public final String SCOPE = "offline_access Files.ReadWrite";
	static public final String REDIRECTURL = "http://localhost:44316";

	static public Client login() {
		class TestAuthenticator implements Authenticator {
			@Override
			public void requestAuthentication(String requestUrl) {
				try {
					Desktop.getDesktop().browse(new URI(requestUrl));
				}
				catch (Exception e) {
					e.printStackTrace();
					fail("Authenticator failed");
				}
			}
		}

		try {
			OAuthEntry entry = new OAuthEntry(APPID, SCOPE, REDIRECTURL);
			OneDriveParameter parameter = new OneDriveParameter(entry);

			Client client = Client.createInstance(parameter);
			assertNotNull(client);

			TestAuthenticator authenticator = new TestAuthenticator();
			client.login(authenticator);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Login or logout failed");
		}

		return null;
    }
}
