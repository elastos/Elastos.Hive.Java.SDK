package org.elastos.hive.ipfs;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.*;
import org.elastos.hive.Void;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class IpfsDirectoryTest {
	private static Drive drive;
	private static Client client;
	private static Directory testDirectory;
	private static Directory parentDirForMoveTo;

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
		Directory.Info info = testDirectory.getLastInfo();
		assertNotNull(info);
		assertNotNull(info.get(Directory.Info.itemId));
		assertNotNull(info.get(Directory.Info.name));
		assertTrue(info.containsKey(Directory.Info.childCount));
	}

	@Test public void testGetInfo() {
		try {
			Directory.Info info = testDirectory.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(Directory.Info.itemId));
			assertNotNull(info.get(Directory.Info.name));
			assertTrue(info.containsKey(Directory.Info.childCount));
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
			final int NUM = 5;
			for (int i = 0; i < NUM; i++) {
				String childName = "testGetChildrenCount" + System.currentTimeMillis();
				assertNotNull(testDirectory.createFile(childName).get());
			}

			children = testDirectory.getChildren().get();
			assertEquals(count + NUM, children.getContent().size());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildrenCount failed");
		}
	}

	@Test public void testGetChildrenDetails() {
		try {
			Children children = testDirectory.getChildren().get();
			assertNotNull(children);
			final int count = children.getContent().size();

			//Create several files and check the count.
			final int DIR_NUM = 5;
			String[] dirNames = new String[DIR_NUM];
			for (int i = 0; i < DIR_NUM; i++) {
				String childName = "testGetChildrenDetails_dir" + System.currentTimeMillis();
				assertNotNull(testDirectory.createDirectory(childName).get());
				dirNames[i] = childName;
			}

			final int FILE_NUM = 10;
			String[] fileNames = new String[FILE_NUM];
			for (int i = 0; i < FILE_NUM; i++) {
				String childName = "testGetChildrenDetails_file" + System.currentTimeMillis();
				assertNotNull(testDirectory.createFile(childName).get());
				fileNames[i] = childName;
			}

			children = testDirectory.getChildren().get();
			assertEquals(count + DIR_NUM + FILE_NUM, children.getContent().size());

			ArrayList<ItemInfo> items = children.getContent();
			for (ItemInfo info: items) {
				assertNotNull(info);
				assertNotNull(info.get(ItemInfo.itemId));

				String name = info.get(ItemInfo.name);
				assertNotNull(name);

				for (String dirName: dirNames) {
					if (name.equals(dirName)) {
						String path = testDirectory.getPath() + "/" + name;
						ItemInfo dirInfo = drive.getItemInfo(path).get();
						assertNotNull(dirInfo);

						String type = dirInfo.get(ItemInfo.type);
						assertNotNull(type);
						assertEquals("directory", type);
						assertNotNull(dirInfo.get(ItemInfo.size));
					}
				}

				for (String fileName: fileNames) {
					if (name.equals(fileName)) {
						String path = testDirectory.getPath() + "/" + name;
						ItemInfo fileInfo = drive.getItemInfo(path).get();
						assertNotNull(fileInfo);

						String type = fileInfo.get(ItemInfo.type);
						assertNotNull(type);
						assertEquals("file", type);
						assertNotNull(fileInfo.get(ItemInfo.size));
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildrenDetails failed");
		}
	}

	@Test public void testMoveTo() {
		try {
			String originPath = testDirectory.getPath();
			testDirectory.moveTo(parentDirForMoveTo.getPath()).get();

			//1. Check the parent has a new child.
			Children children = parentDirForMoveTo.getChildren().get();
			assertNotNull(children);
			assertEquals(1, children.getContent().size());

			//2. Check: the origin and the new path is different
			assertFalse(originPath.equals(testDirectory.getPath()));

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
			testDirectory.copyTo(parentPath).get();

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
				fail();
			}
		}
	}

	@Test public void testCopyToInvalid() {
		Directory parentDir = null;
		try {
			String parentPath = "/parentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			String originPath = testDirectory.getPath();
			Void placeHolder = testDirectory.copyTo(parentPath).get();

			//1. Check the parent has a new child.
			Children children = parentDir.getChildren().get();
			assertNotNull(children);
			assertEquals(1, children.getContent().size());

			//2. Check: the origin and the new path is same
			assertEquals(originPath, testDirectory.getPath());

			//3. Check: the origin path is valid.
			try {
				drive.getDirectory(originPath).get();
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
				fail();
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
			testDirectory = drive.createDirectory("/testDirectory" + System.currentTimeMillis()).get();
			assertNotNull(testDirectory);
			assertNotNull(testDirectory.getPath());
			parentDirForMoveTo = drive.createDirectory("/parentDirForMoveTo" + System.currentTimeMillis()).get();
			assertNotNull(parentDirForMoveTo);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	try {
    		testDirectory.deleteItem().get();
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
