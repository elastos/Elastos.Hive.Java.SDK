package org.elastos.hive.OneDrive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.elastos.hive.Client;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.File;
import org.elastos.hive.ItemInfo;
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
			assertNotNull(info.get(Drive.Info.driveId));
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

	@Test public void testGetItemInfo() {
		File file = null;
		Directory directory = null;
		try {
			//1. file
			String name = "testGetItemInfo_File.txt";
			String pathName = "/" + name;
			file = drive.createFile(pathName).get();
			assertNotNull(file);

			ItemInfo info = drive.getItemInfo(pathName).get();
			assertNotNull(info);
			assertEquals(name, info.get(ItemInfo.name));
			assertEquals("file", info.get(ItemInfo.type));
			assertEquals(0, Integer.parseInt(info.get(ItemInfo.size)));
			assertNotNull(info.get(ItemInfo.itemId));

			//2. directory
			name = "testGetItemInfo_Dir" + System.currentTimeMillis();
			pathName = "/" + name;
			directory = drive.createDirectory(pathName).get();
			assertNotNull(directory);

			info = drive.getItemInfo(pathName).get();
			assertNotNull(info);
			assertEquals(name, info.get(ItemInfo.name));
			assertEquals("directory", info.get(ItemInfo.type));
			assertEquals(0, Integer.parseInt(info.get(ItemInfo.size)));
			assertNotNull(info.get(ItemInfo.itemId));

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetItemInfo failed");
		}
		finally {
			try {
				file.deleteItem().get();
				directory.deleteItem().get();
			} catch (Exception ex) {
				ex.printStackTrace();
				fail();
			}
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
