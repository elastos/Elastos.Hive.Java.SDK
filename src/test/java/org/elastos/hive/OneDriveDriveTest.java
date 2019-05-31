package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OneDriveDriveTest {
	private static Drive drive;
	private static Client client;

	@Test public void testGetInfo() {
		try {
			Drive.Info info = drive.getInfo().get();
			assertNotNull(info);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
    }

	@Test public void testGetRootDir() {
		try {
			Directory root = drive.getRootDir().get();
			assertNotNull(root);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getRootDir failed");
		}
    }

	@Test public void testCreateDirectory() {
		Directory directory = null;
		try {
			String pathName = "/newOneDriveDir" + System.currentTimeMillis();
			directory = drive.createDirectory(pathName).get();
			assertNotNull(directory);

			pathName += "/" + System.currentTimeMillis();
			Directory secondLevelDir = drive.createDirectory(pathName).get();
			assertNotNull(secondLevelDir);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("createDirectory failed");
		}
		finally {
			try {
				directory.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
		}
    }

	@Test public void testCreateDirectoryWithInvalidArg() {
		try {
			//Must include "/"
			String pathName = "InvalidDirectoryPath";
			drive.createDirectory(pathName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

	@Test public void testGetDirectory() {
		Directory directory = null;
		try {
			String pathName = "/newOneDriveDir" + System.currentTimeMillis();
			directory = drive.createDirectory(pathName).get();
			assertNotNull(directory);

			directory = drive.getDirectory(pathName).get();
			assertNotNull(directory);
		} catch (InterruptedException | ExecutionException e) {
			fail("getDirectory failed");
		}		
		finally {
			try {
				directory.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
		}
    }

	@Test public void testGetDirectoryWithInvalidArg() {
		try {
			//Must include "/"
			String pathName = "InvalidDirectoryPath";
			drive.getDirectory(pathName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

	@Test public void testCreateFile() {
		File file = null;
		try {
			String pathName = "/newOneDriveFile";
			file = drive.createFile(pathName).get();
			assertNotNull(file);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("createFile failed");
		}
		finally {
			try {
				file.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
		}
    }

	@Test public void testCreateFileWithInvalidArg() {
		try {
			//Must include "/"
			String pathName = "InvalidFilePath";
			drive.createFile(pathName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

	@Test public void testGetFile() {
		File file = null;
		try {
			String pathName = "/newOneDriveFile" + System.currentTimeMillis();
			file = drive.createFile(pathName).get();
			assertNotNull(file);
			file = drive.getFile(pathName).get();
			assertNotNull(file);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getFile failed");
		}
		finally {
			try {
				file.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
		}
    }

	@Test public void testGetFileWithInvalidArg() {
		try {
			//Must include "/"
			String pathName = "InvalidFilePath";
			drive.getFile(pathName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

	@BeforeClass
	static public void setUp() throws Exception {
		if (client == null) {
			client = OneDriveTestBase.login();
			assertNotNull(client);
			drive = client.getDefaultDrive().get();
			assertNotNull(drive);
		}
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	if (client != null) {
    		client.logout();
    	}
    }
}
