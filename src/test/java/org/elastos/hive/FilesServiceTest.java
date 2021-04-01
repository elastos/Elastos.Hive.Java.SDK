package org.elastos.hive;

import com.google.common.base.Throwables;
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
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilesServiceTest {
	private static final String FILE_NAME_TXT = "test.txt";
	private static final String FILE_NAME_IMG = "big.png";

	private final String localTxtFilePath;
	private final String localImgFilePath;
	private final String localCacheRootDir;

	private final String remoteRootDir;
	private final String remoteTxtFilePath;
	private final String remoteImgFilePath;
	private final String remoteBackupTxtFilePath;

	private static FilesService filesService;

	public FilesServiceTest() {
		String rootDir = System.getProperty("user.dir") + "/src/test/resources/local/";
		localTxtFilePath = rootDir + FILE_NAME_TXT;
		localImgFilePath = rootDir + FILE_NAME_IMG;
		localCacheRootDir = rootDir + "cache/file/";
		remoteRootDir = "hive";
		remoteTxtFilePath = remoteRootDir + File.separator + FILE_NAME_TXT;
		remoteImgFilePath = remoteRootDir + File.separator + FILE_NAME_IMG;
		remoteBackupTxtFilePath = "backup" + File.separator + FILE_NAME_TXT;
	}

	@BeforeAll
	public static void setUp() {
		try {
			filesService = TestData.getInstance().newVault().getFilesService();
		} catch (HiveException | DIDException e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(1)
	void testUploadText() {
		try (Writer writer = filesService.upload(remoteTxtFilePath, Writer.class).get();
			 FileReader fileReader = new FileReader(localTxtFilePath)) {
			Assertions.assertNotNull(writer);
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
		verifyRemoteFileExists(remoteTxtFilePath);
	}

	@Test
	@Order(2)
	void testUploadBin() {
		try (OutputStream out = filesService.upload(remoteImgFilePath, OutputStream.class).get()) {
			Assertions.assertNotNull(out);
			out.write(Utils.readImage(localImgFilePath));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
		verifyRemoteFileExists(remoteImgFilePath);
	}

	@Test
	@Order(3)
	void testDownloadText() {
		try (Reader reader = filesService.download(remoteTxtFilePath, Reader.class).get()) {
			Assertions.assertNotNull(reader);
			Utils.cacheTextFile(reader, localCacheRootDir, FILE_NAME_TXT);
			Assertions.assertTrue(isFileContentEqual(localTxtFilePath, localCacheRootDir + FILE_NAME_TXT));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(4)
	void testDownloadBin() {
		try (InputStream in = filesService.download(remoteImgFilePath, InputStream.class).get()) {
			Assertions.assertNotNull(in);
			Utils.cacheBinFile(in, localCacheRootDir, FILE_NAME_IMG);
			Assertions.assertTrue(isFileContentEqual(localImgFilePath, localCacheRootDir + FILE_NAME_IMG));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(5)
	void testList() {
		try {
			List<FileInfo> files = filesService.list(remoteRootDir).get();
			Assertions.assertNotNull(files);
			Assertions.assertEquals(files.size(), 2);
			List<String> names = files.stream().map(FileInfo::getName).collect(Collectors.toList());
			Assertions.assertTrue(names.contains(FILE_NAME_TXT));
			Assertions.assertTrue(names.contains(FILE_NAME_IMG));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(6)
	void testHash() {
		try {
			String hash = filesService.hash(remoteTxtFilePath).get();
			Assertions.assertNotNull(hash);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(7)
	void testMove() {
		try {
			Boolean isSuccess = filesService.delete(remoteBackupTxtFilePath)
					.thenCompose(result -> filesService.move(remoteTxtFilePath, remoteBackupTxtFilePath))
					.get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
		verifyRemoteFileExists(remoteBackupTxtFilePath);
	}

	@Test
	@Order(8)
	void testCopy() {
		try {
			Boolean isSuccess = filesService.copy(remoteBackupTxtFilePath, remoteTxtFilePath).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
		verifyRemoteFileExists(remoteTxtFilePath);
	}

	@Test
	@Order(9)
	void testDeleteFile() {
		try {
			Boolean isSuccess = filesService.delete(remoteTxtFilePath)
					.thenCompose(result -> filesService.delete(remoteBackupTxtFilePath))
					.get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private static void verifyRemoteFileExists(String path) {
		verifyRemoteFileExists(filesService, path);
	}

	public static void verifyRemoteFileExists(FilesService filesService, String path) {
		try {
			FileInfo info = filesService.stat(path).get();
			Assertions.assertNotNull(info);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	public static void removeLocalFile(String filePath) {
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
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
