package org.elastos.hive;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilesTest {

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
		CompletableFuture<Boolean> future = filesApi.list(remoteRootPath)
				.handle((result, ex) -> {
					assertTrue(result.size() > 0);
					System.out.println("list size=" + result.size());
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test06_hash() {
		CompletableFuture<Boolean> future = filesApi.hash(remoteTextPath)
				.handle((result, ex) -> {
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test07_move() {
		CompletableFuture<Boolean> future = filesApi.delete(remoteTextBackupPath)
				.thenCompose(result -> filesApi.move(remoteTextPath, remoteTextBackupPath))
				.handle((result, ex) -> {
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test08_copy() {
		CompletableFuture<Boolean> future = filesApi.copy(remoteTextBackupPath, remoteTextPath)
				.handle((result, ex) -> {
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}


	@Test
	public void test09_deleteFile() {
		CompletableFuture<Boolean> future = filesApi.delete(remoteTextPath)
				.thenCompose(result -> filesApi.delete(remoteTextBackupPath))
				.handle((result, ex) -> {
					return (ex == null);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}


	@BeforeClass
	public static void setUp() {
		Vault vault = AppInstanceFactory.getUser2().getVault();
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

	public FilesTest() {
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
