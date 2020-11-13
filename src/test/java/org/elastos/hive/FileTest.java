package org.elastos.hive;

import org.elastos.hive.files.FileInfo;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

	@Test
	public void test01_uploadText() {
		FileReader fileReader = null;
		Writer writer = null;
		try {
			writer = filesApi.upload(remoteTextPath, Writer.class).exceptionally(e -> {
				System.out.println(e.getMessage());
				return null;
			}).get();
			fileReader = new FileReader(new File(textLocalPath));
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
			System.out.println("write success");
		} catch (Exception e) {
			fail();
		} finally {
			try {
				if (null != fileReader) fileReader.close();
				if (null != writer) writer.close();
			} catch (Exception e) {
				fail();
			}
		}
	}


	@Test
	public void test02_uploadBin() {
		try {
			OutputStream outputStream = filesApi.upload(remoteImgPath, OutputStream.class).get();
			byte[] bigStream = Utils.readImage(imgLocalPath);
			outputStream.write(bigStream);
			outputStream.close();
			System.out.println("write success");
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test03_downloadText() {
		try {
			Reader reader = filesApi.download(remoteTextPath, Reader.class).get();
			Utils.cacheTextFile(reader, rootLocalCachePath, "test.txt");
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test04_downloadBin() {
		try {
			InputStream inputStream = filesApi.download(remoteImgPath, InputStream.class).get();
			Utils.cacheBinFile(inputStream, rootLocalCachePath, "big.png");
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test05_list() {
		try {
			List<FileInfo> result = filesApi.list(remoteRootPath).get();
			assertNotNull(result);
			assertTrue(result.size() > 0);
			System.out.println("list size=" + result.size());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test06_hash() {
		try {
			String hash = filesApi.hash(remoteTextPath).get();
			assertNotNull(hash);
			System.out.println("hash=" + hash);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test07_move() {
		try {
			filesApi.delete(remoteTextBackupPath).whenComplete((aBoolean, throwable) -> {
				try {
					boolean success = filesApi.move(remoteTextPath, remoteTextBackupPath).get();
					assertTrue(success);
				} catch (Exception e) {
					fail();
				}
			}).get();
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test08_copy() {
		try {
			boolean success = filesApi.copy(remoteTextBackupPath, remoteTextPath).get();
			assertTrue(success);
		} catch (Exception e) {
			fail();
		}
	}


	@Test
	public void test09_deleteFile() {
		try {
			filesApi.delete(remoteTextPath).get();
			filesApi.delete(remoteTextBackupPath).get();
		} catch (Exception e) {
			fail();
		}
	}


	@BeforeClass
	public static void setUp() {
		Vault vault = UserFactory.createUser2().getVault();
		filesApi = vault.getFiles();
	}

	private final String textLocalPath;
	private final String imgLocalPath;
	private final String rootLocalCachePath;
	private final String textLocalCachePath;
	private final String imgLocalCachePath;

	private final String remoteRootPath;
	private final String remoteTextPath;
	private final String remoteImgPath;
	private final String remoteTextBackupPath;

	private static Files filesApi;

	public FileTest() {
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/";
		textLocalPath = localRootPath +"test.txt";
		imgLocalPath = localRootPath +"big.png";
		rootLocalCachePath = localRootPath + "cache/file/";
		textLocalCachePath = rootLocalCachePath + "test.txt";
		imgLocalCachePath = rootLocalCachePath + "/big.png";

		remoteRootPath = "hive";
		remoteTextPath = remoteRootPath + File.separator + "test.txt";
		remoteImgPath = remoteRootPath + File.separator + "big.png";
		remoteTextBackupPath = "backup" + File.separator + "test.txt";
	}
}
