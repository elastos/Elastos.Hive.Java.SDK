package org.elastos.hive.ipfs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Drive;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IpfsClientTest {
	private static Client client;
	private boolean callbackInvoked = false;

	@Test public void testGetInstance() {
		assertNotNull(Client.getInstance(DriveType.hiveIpfs));
	}

	@Test public void testGetInfo() {
		try {
			Client.Info info = client.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(Client.Info.userId));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}
	
	@Test
	public void testGetInfoAsync(){
		callbackInvoked = false;
		Callback<Client.Info> callback = new Callback<Client.Info>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Client.Info body) {
				callbackInvoked = true;
				Client.Info info = body ;
				assertNotNull(info);
				assertNotNull(info.get(Client.Info.userId));
			}
		};

		try {
			client.getInfo(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfoAsync failed");
		}
	}

	@Test public void testGetDefaultDrive() {
		try {
			Drive drive = client.getDefaultDrive().get();
			assertNotNull(drive);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetDefaultDrive failed");
		}
	}
	
	@Test
	public void testGetDefaultDriveAsync(){
		callbackInvoked = false;
		Callback<Drive> callback = new Callback<Drive>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Drive drive) {
				callbackInvoked = true;
				assertNotNull(drive);
			}
		};

		try {
			client.getDefaultDrive(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetDefaultDriveAsync failed");
		}
	}

	@BeforeClass
	static public void setUp() throws Exception {
		client = IpfsTestBase.login();
		assertNotNull(client);
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
