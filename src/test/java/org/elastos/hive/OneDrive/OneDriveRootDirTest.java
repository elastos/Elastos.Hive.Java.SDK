package org.elastos.hive.OneDrive;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class OneDriveRootDirTest {
	private static Drive drive;
	private static Client client;
	private static Directory root;
	private boolean callbackInvoked = false;
	
	@Test public void testGetId() {
		assertNotNull(root.getId());
	}

	@Test public void testGetPath() {
		assertNotNull(root.getPath());
	}

	@Test public void testGetParentPath() {
		assertNotNull(root.getParentPath());
	}

	@Test public void testGetLastInfo() {
		Directory.Info info = root.getLastInfo();
		assertNotNull(info);
		assertNotNull(info.get(Directory.Info.itemId));
		assertNotNull(info.get(Directory.Info.name));
		assertTrue(info.containsKey(Directory.Info.childCount));
	}

	@Test public void testGetInfo() {
		try {
			Directory.Info info = root.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(Directory.Info.itemId));
			assertNotNull(info.get(Directory.Info.name));
			assertTrue(info.containsKey(Directory.Info.childCount));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("getInfo failed");
		}
	}

	@Test public void testGetInfoAsync() {
		callbackInvoked = false;
		Callback<Directory.Info> callback = new Callback<Directory.Info>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Directory.Info info) {
				callbackInvoked = true;
				assertNotNull(info);
				assertNotNull(info.get(Directory.Info.itemId));
				assertNotNull(info.get(Directory.Info.name));
				assertTrue(info.containsKey(Directory.Info.childCount));
			}
		};
		
		try {
			root.getInfo(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfoAsync failed");
		}
	}

	@Test public void testCreateDirectory() {
		Directory child = null;
		try {
			String childName = "testCreateDirectory" + System.currentTimeMillis();
			child = root.createDirectory(childName).get();
			assertNotNull(child);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateDirectory failed");
		}
		finally {
			try {
				if (child != null) {
					child.deleteItem().get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test public void testCreateDirectoryWithInvalidArg() {
		try {
			String childName = "/testCreateDirectory" + System.currentTimeMillis();
			root.createDirectory(childName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test public void testGetDirectory() {
		Directory child = null;
		try {
			String childName = "testGetDirectory" + System.currentTimeMillis();
			child = root.createDirectory(childName).get();
			assertNotNull(child);

			Directory childDir = root.getDirectory(childName).get();
			assertNotNull(childDir);
			assertEquals(child.getPath(), childDir.getPath());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetDirectory failed");
		}
		finally {
			try {
				if (child != null) {
					child.deleteItem().get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test public void testCreateFile() {
		File child = null;
		try {
			String childName = "testCreateFile" + System.currentTimeMillis();
			child = root.createFile(childName).get();
			assertNotNull(child);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateFile failed");
		}
		finally {
			try {
				if (child != null) {
					child.deleteItem().get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test public void testCreateFileWithInvalidArg() {
		try {
			String childName = "/testCreateFileWithInvalidArg" + System.currentTimeMillis();
			root.createFile(childName).get();
			fail();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test public void testGetFile() {
		File child = null;
		try {
			String childName = "testGetFile" + System.currentTimeMillis();
			child = root.createFile(childName).get();
			assertNotNull(child);
			File childFile = root.getFile(childName).get();
			assertNotNull(childFile);
			assertEquals(child.getPath(), childFile.getPath());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetFile failed");
		}
		finally {
			try {
				if (child != null) {
					child.deleteItem().get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test public void testGetChildren() {
		try {
			Children children = root.getChildren().get();
			assertNotNull(children);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildren failed");
		}
	}

	@Test public void testGetRootChildren() {
		try {
			Directory root = drive.getRootDir().get();
			Children children = root.getChildren().get();
			assertNotNull(children);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildren failed");
		}
	}
	
	@Test public void testGetChildrenCount() {
		try {
			Children children = root.getChildren().get();
			assertNotNull(children);
			final int count = children.getContent().size();

			//Create several files and check the count.
			final int NUM = 5;
			for (int i = 0; i < NUM; i++) {
				String childName = "testGetChildrenCount" + System.currentTimeMillis();
				assertNotNull(root.createFile(childName).get());
			}

			children = root.getChildren().get();
			assertEquals(count + NUM, children.getContent().size());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildrenCount failed");
		}
	}

	@Test public void testGetChildrenDetails() {
		try {
			Children children = root.getChildren().get();
			assertNotNull(children);
			final int count = children.getContent().size();

			//Create several files and check the count.
			final int NUM = 5;
			for (int i = 0; i < NUM; i++) {
				String childName = "testGetChildrenDetails" + System.currentTimeMillis();
				assertNotNull(root.createFile(childName).get());
			}

			children = root.getChildren().get();
			assertEquals(count + NUM, children.getContent().size());

			ArrayList<ItemInfo> items = children.getContent();
			for (ItemInfo info: items) {
				assertNotNull(info);
				assertNotNull(info.get(ItemInfo.itemId));
				assertNotNull(info.get(ItemInfo.name));
				assertTrue(info.get(ItemInfo.type).equals("file") || info.get(ItemInfo.type).equals("directory"));
				assertTrue(info.containsKey(ItemInfo.size));
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildrenDetails failed");
		}
	}

	@Test public void testMoveTo() {
		Directory rootChild = null;
		try {
			rootChild = root.createDirectory("testMoveToDir_"+System.currentTimeMillis()).get();
			File file = root.createFile("testMoveToFile_"+System.currentTimeMillis()).get();
			
			int childCount = rootChild.getChildren().get().getContent().size();

			file.moveTo(rootChild.getPath()).get();
			
			//1. Check the parent has a new child.
			int newChildCount = rootChild.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testMoveTo failed");
		}
		finally {
			try {
				if (rootChild != null) {
					rootChild.deleteItem().get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test public void testCopyTo() {
		Directory rootChild = null;
		try {
			rootChild = root.createDirectory("testMoveToDir_"+System.currentTimeMillis()).get();
			File file = root.createFile("testMoveToFile_"+System.currentTimeMillis()).get();
			
			int childCount = rootChild.getChildren().get().getContent().size();

			file.copyTo(rootChild.getPath()).get();
			
			//1. Check the parent has a new child.
			int newChildCount = rootChild.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCopyTo failed");
		}
		finally {
			try {
				if (rootChild != null) {
					rootChild.deleteItem().get();
				}
			} catch (Exception e) {
				e.printStackTrace();
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
			root = drive.getRootDir().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	client.logout();
    }
}
