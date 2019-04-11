package org.elastos.hive;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.elastos.hive.exceptions.HiveException;
import org.junit.Test;

public class HiveDriveTest {
	@Test public void testSomeMethod() {
		DriveParameters parameters = DriveParameters.createForOneDrive("tom", "all", "https://127.0.0.1:8080");
		try {
			HiveDrive drive = HiveDrive.createInstance(parameters);
			assertTrue("someMethod should return  'true'", drive.someMethod());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Exception", false);
		}
	}

	@Test public void testGetRootDir() {
		//TODO;
		System.out.print("TODO");
		assertTrue("GetRootDir", true);
	}

	@Test public void testLogin() {
		class TestAuthenticator extends Authenticator {
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

		DriveParameters parameters = DriveParameters.createForOneDrive("3d0f9362-0f69-4a45-9ead-f653c2712290",
				"offline_access Files.ReadWrite",
				"http://localhost:44316");

		try {
			HiveDrive drive = HiveDrive.createInstance(parameters);
			assertTrue("Can't create drive instance.", drive != null);

			TestAuthenticator testAuth = new TestAuthenticator();

			drive.login(testAuth);
			drive.logout();
		} catch (HiveException e) {
			e.printStackTrace();
			assertTrue("Test login errror", false);
		}
    }
}
