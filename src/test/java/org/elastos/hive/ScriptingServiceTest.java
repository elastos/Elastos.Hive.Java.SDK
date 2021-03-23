package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.*;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptingServiceTest {
	private static final String CONDITION_NAME = "get_group_messages";
	private static final String NO_CONDITION_NAME = "script_no_condition";
	private static final String UPLOAD_FILE_NAME = "upload_file";
	private static final String DOWNLOAD_FILE_NAME = "download_file";

	@Test
	public void test01_registerScriptFind() {
		try {
			ScriptKvItem filter = new ScriptKvItem().putKv("_id","$params.group_id")
					.putKv("friends", "$callScripter_did");
			Boolean isSuccess = scriptingService.registerScript(CONDITION_NAME,
					new Condition("verify_user_permission", "queryHasResults",
							new ScriptFindBody("test_group", filter)),
					new Executable(CONDITION_NAME, Executable.TYPE_FIND,
							new ScriptFindBody("test_group", filter)),
					false, false).exceptionally(e->{
						fail();
						return null;
				}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test02_registerScriptFindWithoutCondition() {
		try {
			Boolean isSuccess = scriptingService.registerScript(NO_CONDITION_NAME,
					new Executable("get_groups", Executable.TYPE_FIND,
							new ScriptFindBody("groups",
									new ScriptKvItem().putKv("friends","$caller_did"))),
					false, false)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test03_callScriptFindWithoutCondition() {
		//TODO: A bug on node did_scripting.py line 121: return col maybe None.
		try {
			String result = scriptingService.callScript(NO_CONDITION_NAME,
					null, "appId", String.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test04_callScriptUrlFindWithoutCondition() {
		//TODO: A bug on node did_scripting.py line 121: return col maybe None.
		try {
			String result = scriptingService.callScriptUrl(UPLOAD_FILE_NAME,
					null, "appId", String.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test05_uploadFile() {
		registerScriptFileUpload(UPLOAD_FILE_NAME);
		String transactionId = callScriptFileUpload(UPLOAD_FILE_NAME, fileName);
		uploadFileByTransActionId(transactionId);
		FilesServiceTest.verifyRemoteFileExists(filesService, fileName);
	}

	private void registerScriptFileUpload(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFileUploadExecutable(scriptName),
					false, false)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private String callScriptFileUpload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileUploadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("transaction_id"));
			assertNotNull(result);
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	private void uploadFileByTransActionId(String transactionId) {
		try (Writer writer = scriptingService.uploadFile(transactionId, Writer.class).exceptionally(e -> {
			fail();
			return null;
		}).get(); FileReader fileReader = new FileReader(localSrcFilePath)) {
			assertNotNull(writer);
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test06_fileDownload() {
		FilesServiceTest.removeLocalFile(localDstFilePath);
		registerScriptFileDownload(DOWNLOAD_FILE_NAME);
		String transactionId = callScriptFileDownload(DOWNLOAD_FILE_NAME, fileName);
		downloadFileByTransActionId(transactionId);
		assertTrue(FilesServiceTest.isFileContentEqual(localSrcFilePath, localDstFilePath));
	}

	private void registerScriptFileDownload(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFileDownloadExecutable(scriptName),
					false, false)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private String callScriptFileDownload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileUploadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("transaction_id"));
			assertNotNull(result);
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}

	private void downloadFileByTransActionId(String transactionId) {
		try (Reader reader = scriptingService.downloadFile(transactionId, Reader.class).exceptionally(e -> {
			fail();
			return null;
		}).get()) {
			assertNotNull(reader);
			Utils.cacheTextFile(reader, localDstFileRoot, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		try {
			scriptingService = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getScriptingService()).join();
			filesService = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getFilesService()).join();
		} catch (HiveException|DIDException e) {
			e.printStackTrace();
		}
	}

	private static ScriptingService scriptingService;
	private static FilesService filesService;

	private final String localSrcFilePath;
	private final String localDstFileRoot;
	private final String localDstFilePath;
	private final String fileName;

	public ScriptingServiceTest() {
		fileName = "test.txt";
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/local/";
		localSrcFilePath = localRootPath + fileName;
		localDstFileRoot = localRootPath + "cache/script/";
		localDstFilePath = localDstFileRoot + fileName;
	}
}
