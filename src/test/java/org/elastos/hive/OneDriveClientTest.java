package org.elastos.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.junit.Test;

public class OneDriveClientTest {
	@Test public void testNotLogin() {
		Client client = Client.getInstance(DriveType.oneDrive);
		assertTrue(client == null);
		
		OAuthEntry entry = new OAuthEntry(OneDriveTestBase.APPID,
				OneDriveTestBase.SCOPE, OneDriveTestBase.REDIRECTURL);
		OneDriveParameter parameter = new OneDriveParameter(entry);
		assertEquals(DriveType.oneDrive, parameter.getDriveType());

		client = Client.createInstance(parameter);
		assertNotNull(client);
	}
	
	@Test public void testLoginAndLogout() {
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
			OAuthEntry entry = new OAuthEntry("f0f8fdc1-294e-4d5c-b3d8-774147075480",
					"offline_access Files.ReadWrite",
					"http://localhost:44316");
			OneDriveParameter parameter = new OneDriveParameter(entry);

			Client client = Client.createInstance(parameter);
			assertNotNull(client);

			TestAuthenticator authenticator = new TestAuthenticator();
			client.login(authenticator);

			CompletableFuture<ClientInfo> clientInfo = client.getInfo();
			assertNotNull(clientInfo);
			assertNotNull(clientInfo.get().getDisplayName());
			
			Drive drive = client.getDefaultDrive().get();
			assertNotNull(drive);

			System.out.println("testLogin=================1");
			client.logout();
			System.out.println("testLogin=================2");
			
			try {
				drive = client.getDefaultDrive().get();
				fail("Can't get the default drive.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("testLogin=================3");
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test login errror");
		}
    }
}
