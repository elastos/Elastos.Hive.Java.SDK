package org.elastos.hive.OneDrive;

import org.elastos.hive.*;
import org.elastos.hive.Void;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OneDriveFileOperatorTest {
	private static Drive drive;
	private static Client client;

	@Test 
	public void testFileWrite() {
		File file = null;
		try {
			String pathName = "/onedrive_testFileWrite.txt";
			file = drive.createFile(pathName).get();
			assertNotNull(file);

			String localTestFilePath = String.format("%s/%s", System.getProperty("user.dir"), "README.md");
			ByteBuffer writeBuffer = file2ByteBuffer(localTestFilePath);

			//1. write
			file.write(writeBuffer).get();
			file.commit().get();

			//2. read
			ByteBuffer readBuf = ByteBuffer.allocate(writeBuffer.capacity());
			Length lenObj = new Length(0);

			long readLen = 0;
			while (lenObj.getLength() != -1) {
				ByteBuffer tmpBuf = ByteBuffer.allocate(100);
				lenObj = file.read(tmpBuf).get();
				
				int len = (int) lenObj.getLength();
				if (len != -1) {
					readLen += len;

					byte[] bytes = new byte[len];
					tmpBuf.flip();
					tmpBuf.get(bytes, 0, len);
					readBuf.put(bytes);
				}
			}

			//3. check the valid length and buffer.
			assertEquals(writeBuffer.limit(), readLen);
			assertEquals(0, writeBuffer.compareTo(readBuf));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testFileWrite failed");
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
	
	private long totalLength = 0;
	private boolean callbackInvoked = false;
	Callback<Length> lenCallback = new Callback<Length>() {
		@Override
		public void onError(HiveException e) {
			e.printStackTrace();
			fail();
		}

		@Override
		public void onSuccess(Length length) {
			callbackInvoked = true;
			
			long len = length.getLength();
			if (len != -1) {
				totalLength += len;				
			}
		}
	};

	Callback<Void> voidCallback = new Callback<Void>() {
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

	private void reset() {
		totalLength = 0;
		callbackInvoked = false;
	}
	
	@Test 
	public void testFileWriteWithAsync() {
		reset();

		File file = null;
		try {
			String pathName = "/onedrive_testFileWrite_sync.txt";
			file = drive.createFile(pathName).get();
			assertNotNull(file);

			String localTestFilePath = String.format("%s/%s", System.getProperty("user.dir"), "README.md");
			ByteBuffer writeBuffer = file2ByteBuffer(localTestFilePath);
			int capacity = writeBuffer.capacity();

			//1. write
			file.write(writeBuffer, lenCallback).get();
			assertTrue(callbackInvoked);
			assertEquals(capacity, totalLength);

			//reset for commit
			reset();
			file.commit(voidCallback).get();
			assertTrue(callbackInvoked);

			//2. read
			ByteBuffer readBuf = ByteBuffer.allocate(capacity);
			Length lenObj = new Length(0);

			//reset for read
			reset();
			long readLen = 0;
			while (lenObj.getLength() != -1) {
				ByteBuffer tmpBuf = ByteBuffer.allocate(100);

				callbackInvoked = false;
				lenObj = file.read(tmpBuf, lenCallback).get();
				assertTrue(callbackInvoked);

				int len = (int) lenObj.getLength();
				if (len != -1) {
					readLen += len;

					byte[] bytes = new byte[len];
					tmpBuf.flip();
					tmpBuf.get(bytes, 0, len);
					readBuf.put(bytes);
				}
			}

			assertEquals(capacity, totalLength);

			//3. check the valid length and buffer.
			assertEquals(writeBuffer.limit(), readLen);
			assertEquals(0, writeBuffer.compareTo(readBuf));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testFileWriteWithAsync failed");
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
	
	@Test 
	public void testFileWriteWithPosition() {
		File file = null;
		try {
			String pathName = "/onedrive_testFileWriteWithPosition.txt";
			file = drive.createFile(pathName).get();
			assertNotNull(file);

			String localTestFilePath = String.format("%s/%s", System.getProperty("user.dir"), "README.md");
			ByteBuffer writeBuffer = file2ByteBuffer(localTestFilePath);

			//1. write with position
			final int LEN = 100;
			int writeLen = 0;
			while (writeLen < writeBuffer.capacity()) {
				byte[] writeBytes = null;
				
				int remaining = writeBuffer.remaining();
				if (remaining < LEN) {
					writeBytes = new byte[remaining];
					writeBuffer.get(writeBytes, 0, remaining);
				}
				else {
					writeBytes = new byte[LEN];
					writeBuffer.get(writeBytes);
				}

				ByteBuffer buffer = ByteBuffer.wrap(writeBytes);
				Length lenObj = file.write(buffer, writeLen).get();
				long len = lenObj.getLength();
				if (len != -1) {
					writeLen += len;
				}
			}

			file.commit().get();

			//2. read
			ByteBuffer readBuf = ByteBuffer.allocate(writeBuffer.capacity());
			Length lenObj = new Length(0);

			long readLen = 0;
			while (lenObj.getLength() != -1) {
				ByteBuffer tmpBuf = ByteBuffer.allocate(1000);
				lenObj = file.read(tmpBuf, readLen).get();
				
				int len = (int) lenObj.getLength();
				if (len != -1) {
					readLen += len;

					byte[] bytes = new byte[len];
					tmpBuf.flip();
					tmpBuf.get(bytes, 0, len);
					readBuf.put(bytes);
				}
			}

			//3. check the valid length and buffer.
			assertEquals(writeBuffer.limit(), readLen);
			assertEquals(0, writeBuffer.compareTo(readBuf));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testFileWriteWithPosition failed");
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
	
	@Test
	public void testFileWriteWithPositionAsync() {
		reset();

		File file = null;
		try {
			String pathName = "/onedirve_testFileWriteWithPosition_sync.txt";
			file = drive.createFile(pathName).get();
			assertNotNull(file);

			String localTestFilePath = String.format("%s/%s", System.getProperty("user.dir"), "README.md");
			ByteBuffer writeBuffer = file2ByteBuffer(localTestFilePath);
			int capacity = writeBuffer.capacity();

			//1. write with position
			final int LEN = 100;
			int writeLen = 0;
			while (writeLen < capacity) {
				byte[] writeBytes = null;
				
				int remaining = writeBuffer.remaining();
				if (remaining < LEN) {
					writeBytes = new byte[remaining];
					writeBuffer.get(writeBytes, 0, remaining);
				}
				else {
					writeBytes = new byte[LEN];
					writeBuffer.get(writeBytes);
				}

				ByteBuffer buffer = ByteBuffer.wrap(writeBytes);
				
				callbackInvoked = false;
				Length lenObj = file.write(buffer, writeLen, lenCallback).get();
				assertTrue(callbackInvoked);

				long len = lenObj.getLength();
				if (len != -1) {
					writeLen += len;
				}
			}
			
			assertEquals(capacity, totalLength);

			//reset for commit
			reset();
			file.commit(voidCallback).get();
			assertTrue(callbackInvoked);

			//2. read
			ByteBuffer readBuf = ByteBuffer.allocate(writeBuffer.capacity());
			Length lenObj = new Length(0);

			//reset for read
			reset();
			long readLen = 0;
			while (lenObj.getLength() != -1) {
				ByteBuffer tmpBuf = ByteBuffer.allocate(1000);
				
				callbackInvoked = false;
				lenObj = file.read(tmpBuf, readLen, lenCallback).get();
				assertTrue(callbackInvoked);
				
				int len = (int) lenObj.getLength();
				if (len != -1) {
					readLen += len;

					byte[] bytes = new byte[len];
					tmpBuf.flip();
					tmpBuf.get(bytes, 0, len);
					readBuf.put(bytes);
				}
			}
			
			assertEquals(capacity, totalLength);

			//3. check the valid length and buffer.
			assertEquals(writeBuffer.limit(), readLen);
			assertEquals(0, writeBuffer.compareTo(readBuf));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("testFileWriteWithPositionAsync failed");
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

	private ByteBuffer file2ByteBuffer(String path) {
		FileInputStream fileInputStream = null;
		FileChannel inChannel = null;
		try {
			fileInputStream = new FileInputStream(path);
			inChannel = fileInputStream.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(fileInputStream.available());
			inChannel.read(buffer);
			buffer.flip();
			return buffer;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (inChannel != null) {
					inChannel.close();
				}

				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private void ByteBuffer2File(String path, ByteBuffer buffer) {
		FileChannel outputChannel = null;
		FileOutputStream outputStream = null;
		try {
			buffer.flip();
			java.io.File file = new java.io.File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			outputStream = new FileOutputStream(path, true);
			outputChannel = outputStream.getChannel();
			outputChannel.write(buffer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				outputChannel.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				outputStream.close();
			}
			catch (Exception e) {
				e.printStackTrace();
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
