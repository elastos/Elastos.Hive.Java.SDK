package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.FileInfo;
import org.elastos.hive.service.FilesService;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilesServiceTest {
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

	private static FilesService filesService;

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

	@BeforeAll
	public static void setUp() {
		try {
			filesService = TestData.getInstance().newVault().getFilesService();
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
		}
	}

	@Test
	@Order(1)
	public void testUploadText() {
		try (Writer writer = filesService.upload(remoteTextPath, Writer.class).exceptionally(e -> {
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
	@Order(2)
	public void testUploadBin() {
		try (OutputStream out = filesService.upload(remoteImgPath, OutputStream.class).exceptionally(e->{
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
		verifyRemoteFileExists(remoteImgPath);
	}

	@Test
	@Order(3)
	public void testDownloadText() {
		try (Reader reader = filesService.download(remoteTextPath, Reader.class).exceptionally(e->{
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
	@Order(4)
	public void testDownloadBin() {
		try (InputStream in = filesService.download(remoteImgPath, InputStream.class).exceptionally(e->{
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
	@Order(5)
	public void testList() {
		try {
			List<FileInfo> files = filesService.list(remoteRootPath).exceptionally(e->{
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
	@Order(6)
	public void testHash() {
		try {
			String hash = filesService.hash(remoteTextPath).exceptionally(e->{
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
	@Order(7)
	public void testMove() {
		try {
			Boolean isSuccess = filesService.delete(remoteTextBackupPath)
					.thenCompose(result -> filesService.move(remoteTextPath, remoteTextBackupPath))
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
	@Order(8)
	public void testCopy() {
		try {
			Boolean isSuccess = filesService.copy(remoteTextBackupPath, remoteTextPath)
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
	@Order(9)
	public void testDeleteFile() {
		try {
			Boolean isSuccess = filesService.delete(remoteTextPath)
					.thenCompose(result -> filesService.delete(remoteTextBackupPath))
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

	private static void verifyRemoteFileExists(String path) {
		verifyRemoteFileExists(filesService, path);
	}

	public static void verifyRemoteFileExists(FilesService filesService, String path) {
		try {
			FileInfo info = filesService.stat(path).exceptionally(s->{
				fail();
				return null;
			}).get();
			assertNotNull(info);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public static void removeLocalFile(String filePath) {
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	public static boolean isFileContentEqual(String srcFile, String dstFile) {
		try {
			Path file1 = Paths.get(srcFile);
			Path file2 = Paths.get(dstFile);
			final long size;
			size = Files.size(file1);
			if (size != Files.size(file2))
				return false;

			if (size < 4096)
				return Arrays.equals(Files.readAllBytes(file1), Files.readAllBytes(file2));

			try (InputStream is1 = Files.newInputStream(file1);
				 InputStream is2 = Files.newInputStream(file2)) {
				// Compare byte-by-byte.
				// Note that this can be sped up drastically by reading large chunks
				// (e.g. 16 KBs) but care must be taken as InputStream.read(byte[])
				// does not neccessarily read a whole array!
				int data;
				while ((data = is1.read()) != -1)
					if (data != is2.read())
						return false;
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
