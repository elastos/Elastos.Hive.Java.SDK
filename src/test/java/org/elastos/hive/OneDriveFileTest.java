package org.elastos.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OneDriveFileTest {
	private static Drive drive;
	private static Client client;
	private static File testFile;
	private static Directory parentDirForMoveTo;

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
		assertNotNull(testFile.getLastInfo());
	}

	@Test public void testGetInfo() {
		try {
			File.Info info = testFile.getInfo().get();
			assertNotNull(info);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}

	@Test public void testMoveToInvalid() {
		try {
			String originPath = testFile.getPath();
			testFile.moveTo(parentDirForMoveTo.getPath()).get();

			//1. Check the parent has a new child.
			Children children = parentDirForMoveTo.getChildren().get();
			assertNotNull(children);
			assertEquals(1, children.getContent().size());

			//2. Check: the origin and the new path is different
			assertNotEquals(originPath, testFile.getPath());

			//3. Check: the origin path is invalid.
			try {
				drive.getDirectory(originPath).get();
				fail(String.format("The a directory has moved, the origin path is invalid: %s", originPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testMoveToInvalid failed");
		}
	}
	
	@Test public void testCopyTo() {
		Directory parentDir = null;
		try {
			String parentPath = "/parentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			Status status = testFile.copyTo(parentPath).get();
			assertEquals(1, status.getStatus());

			Thread.sleep(5000);
			Children children = parentDir.getChildren().get();
			assertNotNull(children);
			assertEquals(1, children.getContent().size());
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

	@Test public void testCopyToInvalid() {
		Directory parentDir = null;
		try {
			String parentPath = "/parentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			String originPath = testFile.getPath();
			Status status = testFile.copyTo(parentPath).get();
			assertEquals(1, status.getStatus());

			//1. Check the parent has a new child.
			Thread.sleep(2000);
			Children children = parentDir.getChildren().get();
			assertNotNull(children);
			assertEquals(1, children.getContent().size());

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
			client = OneDriveTestBase.login();
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
