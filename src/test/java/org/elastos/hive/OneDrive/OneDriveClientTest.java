package org.elastos.hive.OneDrive;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OneDriveClientTest {
	private static Client client;
	@Test public void testGetInstance() {
		assertNotNull(Client.getInstance(DriveType.oneDrive));
	}

	@Test public void testGetInfo() {
		try {
			Client.Info info = client.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(Client.Info.userId));
			assertTrue(info.containsKey(Client.Info.name));
			assertTrue(info.containsKey(Client.Info.email));
			assertTrue(info.containsKey(Client.Info.phoneNo));
			assertTrue(info.containsKey(Client.Info.region));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}

	@Test
	public void testGetInfoAsync(){
		Callback<Client.Info> callback = new Callback<Client.Info>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
			}

			@Override
			public void onSuccess(Client.Info body) {
				Client.Info info = body ;
				assertNotNull(info);
				assertNotNull(info.get(Client.Info.userId));
				assertNotNull(info.get(Client.Info.name));
			}
		};

		client.getInfo(callback);
	}

	@Test public void testGetDefaultDrive() {
		try {
			Drive drive = client.getDefaultDrive().get();
			assertNotNull(drive);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}

	@Test
	public void testGetDefaultDriveAsync(){
		Callback<Drive> callback = new Callback<Drive>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
			}

			@Override
			public void onSuccess(Drive body) {
				Drive drive = body ;
				assertNotNull(body);
			}
		};
		client.getDefaultDrive(callback);
	}

	@BeforeClass
	static public void setUp() throws Exception {
		client = OneDriveTestBase.login();
		assertNotNull(client);
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
