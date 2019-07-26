package org.elastos.hive.ipfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Directory;
import org.elastos.hive.Drive;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.ItemInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IpfsDriveTest {
	private static Drive drive;
	private static Client client;
	private boolean callbackInvoked = false;

	@Test public void testGetInfo() {
		try {
			Drive.Info info = drive.getInfo().get();
			assertNotNull(info);
			assertNotNull(info.get(Drive.Info.driveId));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfo failed");
		}
    }
	
	@Test public void testGetInfoAsync() {
		callbackInvoked = false;
		Callback<Drive.Info> callback = new Callback<Drive.Info>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(Drive.Info info) {
				callbackInvoked = true;
				assertNotNull(info);
				assertNotNull(info.get(Drive.Info.driveId));
			}
		};
		
		try {
			drive.getInfo(callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetInfoAsync failed");
		}
    }

	@Test public void testGetLastInfo() {
		Drive.Info info = drive.getLastInfo();
		assertNotNull(info);
		assertNotNull(info.get(Drive.Info.driveId));
    }
	
	@Test public void testGetRootDir() {
		try {
			Directory root = drive.getRootDir().get();
			assertNotNull(root);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetRootDir failed");
		}
    }
	
	@Test public void testGetRootDirAsync() {
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
			fail("testGetRootDirAsync failed");
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
	
	private Directory testDirectory = null;
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
				testDirectory = directory;
			}
		};

		try {
			String pathName = "/testCreateDirectoryAsync" + System.currentTimeMillis();
			drive.createDirectory(pathName, callback).get();
			assertTrue(callbackInvoked);

			pathName += "/" + System.currentTimeMillis();
			Directory secondLevelDir = drive.createDirectory(pathName).get();
			assertNotNull(secondLevelDir);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateDirectoryAsync failed");
		}
		finally {
			try {
				testDirectory.deleteItem().get();
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
	
	@Test public void testGetDirectoryAsync() {
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
				testDirectory = directory;
			}
		};

		try {
			String pathName = "/testGetDirectoryAsync" + System.currentTimeMillis();
			assertNotNull(drive.createDirectory(pathName).get());

			drive.getDirectory(pathName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			fail("testGetDirectoryAsync failed");
		}		
		finally {
			try {
				testDirectory.deleteItem().get();
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
	
	private File testFile = null;
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
				testFile = file;
			}
		};

		try {
			String pathName = "/testCreateFileAsync";
			drive.createFile(pathName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testCreateFileAsync failed");
		}
		finally {
			try {
				testFile.deleteItem().get();
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
	
	@Test public void testGetFileAsync() {
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
				testFile = file;
			}
		};

		try {
			String pathName = "/testGetFileAsync" + System.currentTimeMillis();
			assertNotNull(drive.createFile(pathName).get());
			drive.getFile(pathName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetFileAsync failed");
		}
		finally {
			try {
				testFile.deleteItem().get();
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
			name = "testGetItemInfo_Dir";
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
	
	@Test public void testGetItemInfoAsync() {
		callbackInvoked = false;
		Callback<ItemInfo> callback = new Callback<ItemInfo>() {
			@Override
			public void onError(HiveException e) {
				e.printStackTrace();
				fail();
			}

			@Override
			public void onSuccess(ItemInfo info) {
				callbackInvoked = true;
				assertNotNull(info);
				assertTrue(info.containsKey(ItemInfo.name));
				assertTrue(info.containsKey(ItemInfo.type));
				assertTrue(info.containsKey(ItemInfo.itemId));
			}
		};

		try {
			//1. file
			String name = "testGetItemInfoAsync_File.txt";
			String pathName = "/" + name;
			testFile = drive.createFile(pathName).get();
			assertNotNull(testFile);

			drive.getItemInfo(pathName, callback).get();
			assertTrue(callbackInvoked);
			
			//2. directory
			name = "testGetItemInfoAsync_Dir";
			pathName = "/" + name;
			testDirectory = drive.createDirectory(pathName).get();
			assertNotNull(testDirectory);

			callbackInvoked = false;
			drive.getItemInfo(pathName, callback).get();
			assertTrue(callbackInvoked);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testGetItemInfoAsync failed");
		}
		finally {
			try {
				testFile.deleteItem().get();
				testDirectory.deleteItem().get();
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
    }
	
	@BeforeClass
	static public void setUp() throws Exception {
		if (client == null) {
			client = IpfsTestBase.login();
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
