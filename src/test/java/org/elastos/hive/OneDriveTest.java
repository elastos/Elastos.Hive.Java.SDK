package org.elastos.hive;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.vendors.onedrive.OneDriveClient;
import org.elastos.hive.vendors.onedrive.OneDriveDrive;
import org.elastos.hive.vendors.onedrive.OneDriveParameter;
import org.junit.Before;
import org.junit.Test;

public class OneDriveTest {
	private static OneDriveDrive drive;
	
	@Test public void testCreateFile() {
		checkLogin();

		String fileName = "/newfilename.txt";
		CompletableFuture<File> file = drive.createFile(fileName);
		try {
			file.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test public void testCreateDirectory() {
		checkLogin();
		//TODO
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
			OAuthEntry entry = new OAuthEntry("f0f8fdc1-294e-4d5c-b3d8-774147075480",
					"offline_access Files.ReadWrite",
					"http://localhost:44316");
			OneDriveParameter parameter = new OneDriveParameter(entry);

			OneDriveClient client = (OneDriveClient)OneDriveClient.createInstance(parameter);

			TestAuthenticator authenticator = new TestAuthenticator();
			client.login(authenticator);

			CompletableFuture<Drive> d = client.getDefaultDrive();
			drive = (OneDriveDrive) d.get();
			assertTrue(drive != null);

			isLogin = true;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Test login errror", false);
		}
    }

	private static boolean isLogin = false;
	@Before
	public void setUp() throws Exception {
		if (!isLogin) {
			testLogin();
		}
	}

	void checkLogin() {
		if (!isLogin) {
			assertTrue("Please login first", false);
		}
	}
}
