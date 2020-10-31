package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.elastos.hive.Files;
import org.elastos.hive.Vault;
import org.elastos.hive.files.FileInfo;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

	private String textLocalPath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";
	private String imgLocalPath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/big.png";

	private String textLocalCachePath = System.getProperty("user.dir") + File.separator + "store/cache/test.txt";
	private String imgLocalCachePath = System.getProperty("user.dir") + File.separator + "store/cache/big.png";

	private static String remoteFolder = "hive";
	private static String remoteTextPath = remoteFolder + File.separator + "test.txt";
	private static String remoteImgPath = remoteFolder + File.separator + "big.png";

	private static String remoteTextBackupPath = "backup" + File.separator + "test.txt";
	private static String remoteImgBackupPath = "backup" + File.separator + "big.png";

	private static Files filesApi;

	@Test
	public void test00_clean() {
		try {
			Utils.deleteFile(textLocalCachePath);
			Utils.deleteFile(imgLocalCachePath);
			filesApi.delete(remoteTextPath).get();
			filesApi.delete(remoteImgPath).get();
			filesApi.delete(remoteTextBackupPath).get();
			filesApi.delete(remoteImgBackupPath).get();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

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
				if(null!=fileReader) fileReader.close();
				if(null!=writer) writer.close();
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
			Utils.cacheTextFile(reader, textLocalCachePath);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test04_downloadBin() {
		try {
			InputStream inputStream = filesApi.download(remoteImgPath, InputStream.class).get();
			Utils.cacheBinFile(inputStream, imgLocalCachePath);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test05_list() {
		try {
			List<FileInfo> result = filesApi.list(remoteFolder).get();
			assertNotNull(result);
			assertTrue(result.size()>0);
			System.out.println("list size=" + result.size());
			for(FileInfo fileInfo : result) {
				System.out.println("type=" + fileInfo.getType());
				System.out.println("name=" + fileInfo.getName());
				System.out.println("fileSize=" + fileInfo.getSize());
				System.out.println("lastModify=" + fileInfo.getLastModified());
			}
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
	public void test07_copy() {
		try {
			boolean success = filesApi.copy(remoteTextPath, remoteTextBackupPath).get();
			assertTrue(success);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test08_move() {
		try {
			boolean success = filesApi.move(remoteImgPath, remoteImgBackupPath).get();
			assertTrue(success);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test09_deleteFile() {
		try {
			filesApi.delete(remoteTextBackupPath).get();
			filesApi.delete(remoteImgBackupPath).get();
		} catch (Exception e) {
			fail();
		}
	}


	@BeforeClass
	public static void setUp() {
		Vault vault = TestFactory.createFactory().getVault();
		filesApi = vault.getFiles();
	}
}
