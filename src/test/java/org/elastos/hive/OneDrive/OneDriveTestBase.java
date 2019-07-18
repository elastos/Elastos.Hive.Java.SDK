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
	private static final String APPID = "afd3d647-a8b7-4723-bf9d-1b832f43b881";//f0f8fdc1-294e-4d5c-b3d8-774147075480
	private static final String SCOPE = "User.Read Files.ReadWrite.All offline_access";//offline_access Files.ReadWrite
	private static final String REDIRECTURL = "http://localhost:12345";//http://localhost:44316

	public static Client login() {
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
