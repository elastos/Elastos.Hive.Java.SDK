package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.connection.UploadStream;
import org.elastos.hive.connection.UploadWriter;
import org.elastos.hive.exception.AlreadyExistsException;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.vault.files.FileInfo;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilesServiceTest {
	private static final String FILE_NAME_TXT = "test.txt";
	private static final String FILE_NAME_IMG = "big.png";
	private static final String FILE_NAME_NOT_EXISTS = "not_exists";

	private static final String FILE_PUBLIC_NAME_TXT = "ipfs_public_file.txt";
	private static final String FILE_PUBLIC_NAME_BIN = "ipfs_public_file.png";

	private final String localTxtFilePath;
	private final String localImgFilePath;
	private final String localCacheRootDir;

	private final String remoteRootDir;
	private final String remoteTxtFilePath;
	private final String remoteImgFilePath;
	private final String remoteNotExistsFilePath;
	private final String remoteNotExistsDirPath;
	private final String remoteBackupTxtFilePath;

	private static VaultSubscription subscription;
	private static FilesService filesService;

	public FilesServiceTest() {
		String rootDir = System.getProperty("user.dir") + "/src/test/resources/local/";
		localTxtFilePath = rootDir + FILE_NAME_TXT;
		localImgFilePath = rootDir + FILE_NAME_IMG;
		localCacheRootDir = rootDir + "cache/file/";
		remoteRootDir = "hive";
		remoteTxtFilePath = remoteRootDir + "/" + FILE_NAME_TXT;
		remoteImgFilePath = remoteRootDir + "/" + FILE_NAME_IMG;
		remoteNotExistsFilePath = remoteRootDir + "/" + FILE_NAME_NOT_EXISTS;
		remoteNotExistsDirPath = remoteNotExistsFilePath;
		remoteBackupTxtFilePath = remoteRootDir + "/" + FILE_NAME_TXT + "2";
	}

	@BeforeAll public static void setUp() throws ExecutionException, InterruptedException {
		trySubscribeVault();
		Assertions.assertDoesNotThrow(()->filesService = TestData.getInstance().newVault().getFilesService());
	}

	private static void trySubscribeVault() throws InterruptedException, ExecutionException {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
		try {
			subscription.subscribe().get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof AlreadyExistsException) {}
			else throw e;
		}
	}

	@Test @Order(1) void testUploadText() {
		Assertions.assertDoesNotThrow(this::uploadTextReally);
		verifyRemoteFileExists(remoteTxtFilePath);
	}

	private void uploadTextReally() throws IOException, ExecutionException, InterruptedException {
		try (UploadWriter writer = filesService.getUploadWriter(remoteTxtFilePath).get();
			 FileReader fileReader = new FileReader(localTxtFilePath)) {
			Assertions.assertNotNull(writer);
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
			writer.flush();
		}
	}

	@Test @Order(2) void testUploadBin() {
		Assertions.assertDoesNotThrow(this::uploadBinReally);
		verifyRemoteFileExists(remoteImgFilePath);
	}

	private void uploadBinReally() throws ExecutionException, InterruptedException, IOException {
		try (UploadStream out = filesService.getUploadStream(remoteImgFilePath).get()) {
			Assertions.assertNotNull(out);
			out.write(Utils.readImage(localImgFilePath));
			out.flush();
		}
	}

	@Test @Order(3) void testDownloadText() {
		Assertions.assertDoesNotThrow(this::downloadTextReally);
	}

	private void downloadTextReally() throws ExecutionException, InterruptedException, IOException {
		try (Reader reader = filesService.getDownloadReader(remoteTxtFilePath).get()) {
			Assertions.assertNotNull(reader);
			Utils.cacheTextFile(reader, localCacheRootDir, FILE_NAME_TXT);
			Assertions.assertTrue(isFileContentEqual(localTxtFilePath, localCacheRootDir + FILE_NAME_TXT));
		}
	}

	@Test @Order(4) void testDownloadBin() {
		Assertions.assertDoesNotThrow(this::downloadBinReally);
	}

	private void downloadBinReally() throws ExecutionException, InterruptedException, IOException {
		try (InputStream in = filesService.getDownloadStream(remoteImgFilePath).get()) {
			Assertions.assertNotNull(in);
			Utils.cacheBinFile(in, localCacheRootDir, FILE_NAME_IMG);
			Assertions.assertTrue(isFileContentEqual(localImgFilePath, localCacheRootDir + FILE_NAME_IMG));
		}
	}

	@Test @Order(4) void testDownloadBin4NotFoundException() {
		ExecutionException e = Assertions.assertThrows(ExecutionException.class,
				() -> filesService.getDownloadStream(remoteNotExistsFilePath).get());
		Assertions.assertEquals(e.getCause().getClass(), NotFoundException.class);
	}

	@Test @Order(5) void testList() {
		Assertions.assertDoesNotThrow(() -> {
			List<FileInfo> files = filesService.list(remoteRootDir).get();
			Assertions.assertNotNull(files);
			Assertions.assertTrue(files.size() >= 2);
			List<String> names = files.stream().map(FileInfo::getName).collect(Collectors.toList());
			Assertions.assertTrue(names.contains(remoteTxtFilePath));
			Assertions.assertTrue(names.contains(remoteImgFilePath));
		});
	}

	@Test @Order(5) void testList4NotFoundException() {
		ExecutionException e = Assertions.assertThrows(ExecutionException.class,
				() -> filesService.list(remoteNotExistsDirPath).get());
		Assertions.assertEquals(e.getCause().getClass(), NotFoundException.class);
	}

	@Test @Order(6) void testHash() {
		Assertions.assertDoesNotThrow(() -> Assertions.assertNotNull(
				filesService.hash(remoteTxtFilePath).get()));
	}

	@Test @Order(6) void testHash4NotFoundException() {
		ExecutionException e = Assertions.assertThrows(ExecutionException.class,
				() -> filesService.hash(remoteNotExistsFilePath).get());
		Assertions.assertEquals(e.getCause().getClass(), NotFoundException.class);
	}

	@Test @Order(7) void testMove() {
		Assertions.assertDoesNotThrow(() -> filesService.move(remoteTxtFilePath, remoteBackupTxtFilePath).get());
		verifyRemoteFileExists(remoteBackupTxtFilePath);
	}

	@Test @Order(7) void testMove4NotFoundException() {
		ExecutionException e = Assertions.assertThrows(ExecutionException.class,
				() -> filesService.move(remoteNotExistsFilePath, remoteNotExistsFilePath + "_bak").get());
		Assertions.assertEquals(e.getCause().getClass(), NotFoundException.class);
	}

	@Test @Order(8) void testCopy() {
		Assertions.assertDoesNotThrow(() ->
				filesService.copy(remoteBackupTxtFilePath, remoteTxtFilePath).get());
		verifyRemoteFileExists(remoteTxtFilePath);
	}

	@Test @Order(8) void testCopy4NotFoundException() {
		ExecutionException e = Assertions.assertThrows(ExecutionException.class,
				() -> filesService.copy(remoteNotExistsFilePath, remoteNotExistsFilePath + "_bak").get());
		Assertions.assertEquals(e.getCause().getClass(), NotFoundException.class);
	}

	@Test @Order(9) void testDeleteFile() {
		Assertions.assertDoesNotThrow(() ->
				filesService.delete(remoteTxtFilePath)
						.thenCompose(result -> filesService.delete(remoteBackupTxtFilePath))
						.get());
	}

	@Test @Order(10) void testUploadPublicText() {
		Assertions.assertDoesNotThrow(() -> {
			String fileName = FILE_PUBLIC_NAME_TXT;
			String scriptName = FILE_PUBLIC_NAME_TXT.split("\\.")[0];
			String cid = null;
			// Upload public file.
			try (UploadWriter writer = filesService.getUploadWriter(fileName, scriptName).get();
				 FileReader fileReader = new FileReader(localTxtFilePath)) {
				Assertions.assertNotNull(writer);
				char[] buffer = new char[1];
				while (fileReader.read(buffer) != -1) {
					writer.write(buffer);
				}
				writer.flush();
				cid = writer.getCid();
			}
			// Download and verify normally.
			try (Reader reader = filesService.getDownloadReader(fileName).get()) {
				Assertions.assertNotNull(reader);
				Utils.cacheTextFile(reader, localCacheRootDir, FILE_NAME_TXT);
				Assertions.assertTrue(isFileContentEqual(localTxtFilePath, localCacheRootDir + FILE_NAME_TXT));
			}
			// Download by cid.
			try (Reader reader = new IpfsRunner(TestData.getInstance().getIpfsGatewayUrl()).getFileReader(cid).get()) {
				Assertions.assertNotNull(reader);
				Utils.cacheTextFile(reader, localCacheRootDir, FILE_NAME_TXT);
				Assertions.assertTrue(isFileContentEqual(localTxtFilePath, localCacheRootDir + FILE_NAME_TXT));
			}
			// Download by script.
			ScriptingServiceTest scriptingServiceTest = new ScriptingServiceTest();
			ScriptingServiceTest.setUp();
			scriptingServiceTest.downloadPublicTxtFileAndVerify(scriptName, localCacheRootDir, FILE_NAME_TXT, localTxtFilePath);
			// clean file and script
			scriptingServiceTest.unregisterScript(scriptName);
			filesService.delete(fileName).get();
		});
	}

	@Test @Order(10) void testUploadPublicBin() {
		Assertions.assertDoesNotThrow(() -> {
			String fileName = FILE_PUBLIC_NAME_BIN;
			String scriptName = FILE_PUBLIC_NAME_BIN.split("\\.")[0];
			String cid = null;
			// Upload public file.
			try (UploadStream out = filesService.getUploadStream(fileName, scriptName).get()) {
				Assertions.assertNotNull(out);
				out.write(Utils.readImage(localImgFilePath));
				out.flush();
				cid = out.getCid();
			}
			// Download and verify normally.
			try (InputStream in = filesService.getDownloadStream(fileName).get()) {
				Assertions.assertNotNull(in);
				Utils.cacheBinFile(in, localCacheRootDir, FILE_NAME_IMG);
				Assertions.assertTrue(isFileContentEqual(localImgFilePath, localCacheRootDir + FILE_NAME_IMG));
			}
			// Download by cid.
			try (InputStream in = new IpfsRunner(TestData.getInstance().getIpfsGatewayUrl()).getFileStream(cid).get()) {
				Assertions.assertNotNull(in);
				Utils.cacheBinFile(in, localCacheRootDir, FILE_NAME_IMG);
				Assertions.assertTrue(isFileContentEqual(localImgFilePath, localCacheRootDir + FILE_NAME_IMG));
			}
			// Download by script.
			ScriptingServiceTest scriptingServiceTest = new ScriptingServiceTest();
			ScriptingServiceTest.setUp();
			scriptingServiceTest.downloadPublicBinFileAndVerify(scriptName, localCacheRootDir, FILE_NAME_IMG, localImgFilePath);
			// clean file and script
			scriptingServiceTest.unregisterScript(scriptName);
			filesService.delete(fileName).get();
		});
	}

	private static void verifyRemoteFileExists(String path) {
		verifyRemoteFileExists(filesService, path);
	}

	public static void verifyRemoteFileExists(FilesService filesService, String path) {
		Assertions.assertDoesNotThrow(() -> Assertions.assertNotNull(filesService.stat(path).get()));
	}

	public static void removeLocalFile(String filePath) {
		Assertions.assertDoesNotThrow(() -> Files.deleteIfExists(Paths.get(filePath)));
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
