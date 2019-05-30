package org.elastos.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

class OneDriveDirectoryTest {
	private static Drive drive;
	private static Client client;
	private static Directory testDirectory;

	@Test public void testRootDirectory() {
		try {
			Directory root = drive.getRootDir().get();
			assertNotNull(root);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testRootDirectory failed");
		}
	}

	@Test public void testGetId() {
		assertNotNull(testDirectory.getId());
	}

	@Test public void testGetPath() {
		assertNotNull(testDirectory.getPath());
	}

	@Test public void testGetParentPath() {
		assertNotNull(testDirectory.getParentPath());
	}

	@Test public void testGetLastInfo() {
		assertNotNull(testDirectory.getLastInfo());
	}

	@Test public void testGetInfo() {
		try {
			DirectoryInfo info = testDirectory.getInfo().get();
			assertNotNull(info);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}

	@Test public void testCreateDirectory() {
		try {
			String childName = "testCreateDirectory" + System.currentTimeMillis();
			Directory child = testDirectory.createDirectory(childName).get();
			assertNotNull(child);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateDirectory failed");
		}
	}

	@Test public void testCreateDirectoryWithInvalidArg() {
		try {
			String childName = "/testCreateDirectory" + System.currentTimeMillis();
			testDirectory.createDirectory(childName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test public void testGetDirectory() {
		try {
			String childName = "testGetDirectory" + System.currentTimeMillis();
			Directory child = testDirectory.createDirectory(childName).get();
			assertNotNull(child);

			Directory childDir = testDirectory.getDirectory(childName).get();
			assertNotNull(childDir);
			assertEquals(child.getPath(), childDir.getPath());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetDirectory failed");
		}
	}

	@Test public void testCreateFile() {
		try {
			String childName = "testCreateFile" + System.currentTimeMillis();
			File child = testDirectory.createFile(childName).get();
			assertNotNull(child);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateFile failed");
		}
	}

	@Test public void testCreateFileWithInvalidArg() {
		try {
			String childName = "/testCreateFileWithInvalidArg" + System.currentTimeMillis();
			testDirectory.createFile(childName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test public void testGetFile() {
		try {
			String childName = "testGetFile" + System.currentTimeMillis();
			File child = testDirectory.createFile(childName).get();
			assertNotNull(child);
			File childFile = testDirectory.getFile(childName).get();
			assertNotNull(childFile);
			assertEquals(child.getPath(), childFile.getPath());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetFile failed");
		}
	}

	@Test public void testGetChildren() {
		try {
			Children children = testDirectory.getChildren().get();
			assertNotNull(children);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildren failed");
		}
	}

	@Test public void testGetChildrenCount() {
		try {
			Children children = testDirectory.getChildren().get();
			assertNotNull(children);
			final int count = children.getContent().size();

			//Create several files and check the count.
			final int newCount = 5;
			for (int i = 0; i < newCount; i++) {
				String childName = "testCreateFile" + System.currentTimeMillis();
				assertNotNull(testDirectory.createFile(childName).get());
			}

			children = testDirectory.getChildren().get();
			assertEquals(count + newCount, children.getContent().size());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildrenCount failed");
		}
	}

	@BeforeClass
	static public void setUp() throws Exception {
		try {
			client = OneDriveTestBase.login();
			assertNotNull(client);
			drive = client.getDefaultDrive().get();
			assertNotNull(drive);
			testDirectory = drive.createDirectory("/testDirectory" + System.currentTimeMillis()).get();
			assertNotNull(testDirectory);
			assertNotNull(testDirectory.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
