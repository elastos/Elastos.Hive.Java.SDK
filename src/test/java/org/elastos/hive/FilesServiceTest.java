package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.FileInfo;
import org.elastos.hive.service.FilesService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilesServiceTest {

	@Test
	public void test01_uploadText() {
		try (Writer writer = filesApi.upload(remoteTextPath, Writer.class).exceptionally(e -> {
			fail();
			return null;
		}).get(); FileReader fileReader = new FileReader(textLocalPath)) {
			assertNotNull(writer);
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		verifyRemoteFileExists(remoteTextPath);
	}

	@Test
	public void test02_uploadBin() {
		try (OutputStream out = filesApi.upload(remoteImgPath, OutputStream.class).exceptionally(e->{
			fail();
			return null;
		}).get()) {
			assertNotNull(out);
			byte[] bigStream = Utils.readImage(imgLocalPath);
			out.write(bigStream);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		verifyRemoteFileExists(remoteTextPath);
	}

	@Test
	public void test03_downloadText() {
		try (Reader reader = filesApi.download(remoteTextPath, Reader.class).exceptionally(e->{
			fail();
			return null;
		}).get()) {
			assertNotNull(reader);
			Utils.cacheTextFile(reader, rootLocalCachePath, "test.txt");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test04_downloadBin() {
		try (InputStream in = filesApi.download(remoteImgPath, InputStream.class).exceptionally(e->{
			fail();
			return null;
		}).get()) {
			assertNotNull(in);
			Utils.cacheBinFile(in, rootLocalCachePath, "big.png");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test05_list() {
		try {
			List<FileInfo> files = filesApi.list(remoteRootPath).exceptionally(e->{
				fail();
				return null;
			}).get();
			assertNotNull(files);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test06_hash() {
		try {
			String hash = filesApi.hash(remoteTextPath).exceptionally(e->{
				fail();
				return null;
			}).get();
			assertNotNull(hash);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test07_move() {
		try {
			Boolean isSuccess = filesApi.delete(remoteTextBackupPath)
					.thenCompose(result -> filesApi.move(remoteTextPath, remoteTextBackupPath))
					.exceptionally(e->{
						fail();
						return false;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		verifyRemoteFileExists(remoteTextBackupPath);
	}

	@Test
	public void test08_copy() {
		try {
			Boolean isSuccess = filesApi.copy(remoteTextBackupPath, remoteTextPath)
					.exceptionally(e->{
						fail();
						return false;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		verifyRemoteFileExists(remoteTextPath);
	}

	@Test
	public void test09_deleteFile() {
		try {
			Boolean isSuccess = filesApi.delete(remoteTextPath)
					.thenCompose(result -> filesApi.delete(remoteTextBackupPath))
					.exceptionally(e->{
						fail();
						return false;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void verifyRemoteFileExists(String path) {
		try {
			FileInfo info = filesApi.stat(path).exceptionally(s->{
				fail();
				return null;
			}).get();
			assertNotNull(info);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		try {
			filesApi = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getFilesService()).join();
		} catch (HiveException|DIDException e) {
			e.printStackTrace();
		}
	}

	private final String textLocalPath;
	private final String imgLocalPath;
	private final String rootLocalCachePath;
	@SuppressWarnings("unused")
	private final String textLocalCachePath;
	@SuppressWarnings("unused")
	private final String imgLocalCachePath;

	private final String remoteRootPath;
	private final String remoteTextPath;
	private final String remoteImgPath;
	private final String remoteTextBackupPath;

	private static FilesService filesApi;

	public FilesServiceTest() {
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/local/";
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
