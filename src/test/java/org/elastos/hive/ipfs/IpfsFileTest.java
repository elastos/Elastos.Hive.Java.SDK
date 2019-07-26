package org.elastos.hive.ipfs;

import java.util.concurrent.ExecutionException;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.Void;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class IpfsFileTest {
	private static Drive drive;
	private static Client client;
	private static File testFile;
	private static Directory parentDirForMoveTo;
	private boolean callbackInvoked = false;

	@Test public void testGetId() {
		assertNotNull(testFile.getId());
	}

	@Test public void testGetPath() {
		assertNotNull(testFile.getPath());
	}

	@Test public void testGetParentPath() {
		assertNotNull(testFile.getParentPath());
	}

	@Test public void testGetLastInfo() {
		File.Info info = testFile.getLastInfo();
		assertNotNull(info);
		assertNotNull(info.get(File.Info.name));
		assertNotNull(info.get(File.Info.itemId));
		assertTrue(info.containsKey(File.Info.size));
	}

	@Test public void testGetInfo() {
		try {
			File.Info info = testFile.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(File.Info.name));
			assertNotNull(info.get(File.Info.itemId));
			assertTrue(info.containsKey(File.Info.size));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfo failed");
		}
	}

	@Test public void testGetInfoAsync() {
		callbackInvoked = false;
		Callback<File.Info> callback = new Callback<File.Info>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(File.Info info) {
				callbackInvoked = true;
				assertNotNull(info);
				assertNotNull(info.get(File.Info.name));
				assertNotNull(info.get(File.Info.itemId));
				assertTrue(info.containsKey(File.Info.size));
			}
		};
		
		try {
			testFile.getInfo(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfoAsync failed");
		}
	}
	
	@Test public void testMoveTo() {
		try {
			File testMoveToFile = drive.createFile("/testMoveToFile" + System.currentTimeMillis()).get();
			assertNotNull(testMoveToFile);
			
			String originPath = testMoveToFile.getPath();
			int childCount = parentDirForMoveTo.getChildren().get().getContent().size();
			testMoveToFile.moveTo(parentDirForMoveTo.getPath()).get();

			//1. Check the parent has a new child.
			int newChildCount = parentDirForMoveTo.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);

			//2. Check: the origin and the new path is different
			assertFalse(originPath.equals(testMoveToFile.getPath()));

			//3. Check: the origin path is invalid.
			try {
				drive.getDirectory(originPath).get();
				fail(String.format("The file has moved, the origin path is invalid: %s", originPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testMoveTo failed");
		}
	}
	
	@Test public void testMoveToAsync() {
		callbackInvoked = false;
		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Void none) {
				callbackInvoked = true;
			}
		};
		
		try {
			String originPath = testFile.getPath();
			int childCount = parentDirForMoveTo.getChildren().get().getContent().size();
			testFile.moveTo(parentDirForMoveTo.getPath(), callback).get();
			assertTrue(callbackInvoked);

			//1. Check the parent has a new child.
			int newChildCount = parentDirForMoveTo.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);

			//2. Check: the origin and the new path is different
			assertFalse(originPath.equals(testFile.getPath()));

			//3. Check: the origin path is invalid.
			try {
				drive.getDirectory(originPath).get();
				fail(String.format("The file has moved, the origin path is invalid: %s", originPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testMoveToAsync failed");
		}
	}

	@Test public void testCopyTo() {
		Directory parentDir = null;
		try {
			String parentPath = "/testCopyToParentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			int childCount = parentDir.getChildren().get().getContent().size();
			testFile.copyTo(parentPath).get();

			int newChildCount = parentDir.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCopyTo failed");
		}
		finally {
			try {
				parentDir.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Test public void testCopyToAsync() {
		callbackInvoked = false;
		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Void none) {
				callbackInvoked = true;
			}
		};

		Directory parentDir = null;
		try {
			String parentPath = "/testCopyToAsyncParentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			int childCount = parentDir.getChildren().get().getContent().size();
			testFile.copyTo(parentPath, callback).get();
			assertTrue(callbackInvoked);

			int newChildCount = parentDir.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCopyToAsync failed");
		}
		finally {
			try {
				parentDir.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Test public void testCopyToInvalid() {
		Directory parentDir = null;
		try {
			String parentPath = "/parentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			String originPath = testFile.getPath();
			int childCount = parentDir.getChildren().get().getContent().size();
			testFile.copyTo(parentPath).get();

			//1. Check the parent has a new child.
			int newChildCount = parentDir.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);

			//2. Check: the origin and the new path is same
			assertEquals(originPath, testFile.getPath());

			//3. Check: the origin path is valid.
			try {
				drive.getFile(originPath).get();
			} catch (Exception e) {
				e.printStackTrace();
				fail(String.format("The origin path should be valid: %s", originPath));
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCopyToInvalid failed");
		}
		finally {
			try {
				parentDir.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@BeforeClass
	static public void setUp() throws Exception {
		try {
			client = IpfsTestBase.login();
			assertNotNull(client);
			drive = client.getDefaultDrive().get();
			assertNotNull(drive);
			testFile = drive.createFile("/testFile" + System.currentTimeMillis()).get();
			assertNotNull(testFile);
			parentDirForMoveTo = drive.createDirectory("/parentDirForMoveTo" + System.currentTimeMillis()).get();
			assertNotNull(parentDirForMoveTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	try {
    		testFile.deleteItem().get();
		} catch (Exception e) {
			e.printStackTrace();
		}

    	try {
    		parentDirForMoveTo.deleteItem().get();
		} catch (Exception e) {
			e.printStackTrace();
		}

    	client.logout();
    }
}
