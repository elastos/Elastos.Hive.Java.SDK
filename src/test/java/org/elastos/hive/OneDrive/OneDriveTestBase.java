package org.elastos.hive.OneDrive;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Client;
import org.elastos.hive.OAuthEntry;
import org.elastos.hive.Parameter;
import org.elastos.hive.vendors.onedrive.OneDriveParameter;

import java.awt.Desktop;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
			//First create OAuthEntry
			OAuthEntry entry = new OAuthEntry(APPID, SCOPE, REDIRECTURL);

			//Then create parameter
			Parameter parameter = new OneDriveParameter(entry, System.getProperty("user.dir"));

			//Finally create client instance
			Client client = Client.createInstance(parameter);
			assertNotNull(client);

			Authenticator authenticator = new TestAuthenticator();
			client.login(authenticator);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
			fail("Login or logout failed");
		}

		return null;
    }
}
