package org.elastos.hive.OneDrive;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.elastos.hive.*;
import org.elastos.hive.Void;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class OneDriveDirectoryTest {
	private static Drive drive;
	private static Client client;
	private static Directory testDirectory;
	private static Directory testDirectoryAsync;
	private static Directory parentDirForMoveTo;
	private boolean callbackInvoked = false;

	@Test public void testRootDirectory() {
		try {
			Directory root = drive.getRootDir().get();
			assertNotNull(root);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testRootDirectory failed");
		}
	}

	@Test public void testRootDirectoryAsync() {
		callbackInvoked = false;
		Callback<Directory> callback = new Callback<Directory>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Directory directory) {
				callbackInvoked = true;
				assertNotNull(directory);
			}
		};
		
		try {
			drive.getRootDir(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testRootDirectoryAsync failed");
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
	
	@Test public void testRootDirectoryGetInfo() {
		try {
			Directory root = drive.getRootDir().get();
			assertNotNull(root);

			Directory.Info info = root.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(Directory.Info.itemId));
			assertNotNull(info.get(Directory.Info.name));
			assertTrue(info.containsKey(Directory.Info.childCount));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testRootDirectoryGetInfo failed");
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
			testDirectory.getInfo(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfoAsync failed");
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

	@Test public void testCreateDirectoryAsync() {
		callbackInvoked = false;
		Callback<Directory> callback = new Callback<Directory>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Directory directory) {
				callbackInvoked = true;
				assertNotNull(directory);
			}
		};

		try {
			String childName = "testCreateDirectoryAsync" + System.currentTimeMillis();
			testDirectory.createDirectory(childName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateDirectoryAsync failed");
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
	
	@Test public void testGetDirectoryAsync() {
		try {
			String childName = "testGetDirectoryAsync" + System.currentTimeMillis();
			Directory child = testDirectory.createDirectory(childName).get();
			assertNotNull(child);

			callbackInvoked = false;
			Callback<Directory> callback = new Callback<Directory>() {
				@Override
				public void onError(HiveException e) {
					e.printStackTrace();
					fail();
				}

				@Override
				public void onSuccess(Directory childDir) {
					callbackInvoked = true;
					assertNotNull(childDir);
					assertEquals(child.getPath(), childDir.getPath());
				}
			};

			testDirectory.getDirectory(childName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetDirectoryAsync failed");
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

	@Test public void testCreateFileAsync() {
		callbackInvoked = false;
		Callback<File> callback = new Callback<File>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(File file) {
				callbackInvoked = true;
				assertNotNull(file);
			}
		};

		try {
			String childName = "testCreateFileAsync" + System.currentTimeMillis();
			testDirectory.createFile(childName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateFileAsync failed");
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
	
	@Test public void testGetFileAsync() {
		try {
			String childName = "testGetFileAsync" + System.currentTimeMillis();
			File child = testDirectory.createFile(childName).get();
			assertNotNull(child);
			
			callbackInvoked = false;
			Callback<File> callback = new Callback<File>() {
				@Override
				public void onError(HiveException e) {
					e.printStackTrace();
					fail();
				}

				@Override
				public void onSuccess(File file) {
					callbackInvoked = true;
					assertNotNull(file);
					assertEquals(child.getPath(), file.getPath());
				}
			};

			testDirectory.getFile(childName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetFileAsync failed");
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
	
	@Test public void testGetChildrenAsync() {
		callbackInvoked = false;
		Callback<Children> callback = new Callback<Children>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Children child) {
				callbackInvoked = true;
				assertNotNull(child);
			}
		};
		
		try {
			testDirectory.getChildren(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetChildrenAsync failed");
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
			final int NUM = 5;
			for (int i = 0; i < NUM; i++) {
				String childName = "testGetChildrenDetails" + System.currentTimeMillis();
				assertNotNull(testDirectory.createFile(childName).get());
			}

			children = testDirectory.getChildren().get();
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
		try {
			String originPath = testDirectory.getPath();
			int childCount = parentDirForMoveTo.getChildren().get().getContent().size();
			testDirectory.moveTo(parentDirForMoveTo.getPath()).get();

			//1. Check the parent has a new child.
			int newChildCount = parentDirForMoveTo.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);

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
			int childCount = parentDirForMoveTo.getChildren().get().getContent().size();
			String originPath = testDirectoryAsync.getPath();
			testDirectoryAsync.moveTo(parentDirForMoveTo.getPath(), callback).get();
			assertTrue(callbackInvoked);

			int newChildCount = parentDirForMoveTo.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);

			//1. Check the parent has a new child.
			Children children = parentDirForMoveTo.getChildren().get();
			assertNotNull(children);
			assertEquals(1, children.getContent().size());

			//2. Check: the origin and the new path is different
			assertFalse(originPath.equals(testDirectoryAsync.getPath()));

			//3. Check: the origin path is invalid.
			try {
				drive.getDirectory(originPath).get();
				fail(String.format("The a directory has moved, the origin path is invalid: %s", originPath));
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
			String parentPath = "/copyToParentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			
			int childCount = parentDir.getChildren().get().getContent().size();
			testDirectory.copyTo(parentPath).get();

			Thread.sleep(5000);
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
				fail();
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
			String parentPath = "/copyToAsyncParentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			
			int childCount = parentDir.getChildren().get().getContent().size();
			testDirectory.copyTo(parentPath, callback).get();
			assertTrue(callbackInvoked);

			Thread.sleep(5000);
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
				fail();
			}
		}
	}

	@Test public void testCopyToInvalid() {
		Directory parentDir = null;
		try {
			String parentPath = "/copyToInvalidParentDir" + System.currentTimeMillis();
			parentDir = drive.createDirectory(parentPath).get();
			assertNotNull(parentDir);
			String originPath = testDirectory.getPath();
			
			int childCount = parentDir.getChildren().get().getContent().size();
			testDirectory.copyTo(parentPath).get();

			//1. Check the parent has a new child.
			Thread.sleep(5000);
			int newChildCount = parentDir.getChildren().get().getContent().size();
			assertEquals(childCount + 1, newChildCount);

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
			client = OneDriveTestBase.login();
			assertNotNull(client);
			drive = client.getDefaultDrive().get();
			assertNotNull(drive);
			testDirectory = drive.createDirectory("/testDirectory" + System.currentTimeMillis()).get();
			assertNotNull(testDirectory);
			assertNotNull(testDirectory.getPath());
			
			testDirectoryAsync = drive.createDirectory("/testDirectoryAsync" + System.currentTimeMillis()).get();
			assertNotNull(testDirectoryAsync);
			assertNotNull(testDirectoryAsync.getPath());
			parentDirForMoveTo = drive.createDirectory("/parentDirForMoveTo" + System.currentTimeMillis()).get();
			assertNotNull(parentDirForMoveTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @AfterClass
    static public void tearDown() throws Exception {
    	try {
    		testDirectory.deleteItem().get();
    		testDirectoryAsync.deleteItem().get();
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
