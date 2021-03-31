package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.*;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;

import static org.junit.Assert.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScriptingServiceTest {
	private static final String FIND_NAME = "get_group_messages";
	private static final String FIND_NO_CONDITION_NAME = "script_no_condition";
	private static final String INSERT_NAME = "database_insert";
	private static final String UPDATE_NAME = "database_update";
	private static final String DELETE_NAME = "database_delete";
	private static final String UPLOAD_FILE_NAME = "upload_file";
	private static final String DOWNLOAD_FILE_NAME = "download_file";
	private static final String FILE_PROPERTIES_NAME = "file_properties";
	private static final String FILE_HASH_NAME = "file_hash";

	private static final String DATABASE_NAME = "script_database";

	private static ScriptingService scriptingService;
	private static FilesService filesService;
	private static DatabaseService databaseService;

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

	@BeforeAll
	public static void setUp() {
		try {
			scriptingService = TestData.getInstance().newVault().getScriptingService();
			filesService = TestData.getInstance().newVault().getFilesService();
			databaseService = TestData.getInstance().newVault().getDatabaseService();
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
		}
		create_test_database();
	}

	@AfterAll
	public static void tearDown() {
		remove_test_database();
	}

	@Test
	@Order(1)
	void testRegisterScriptFind() {
		try {
			KeyValueDict filter = new KeyValueDict().putKv("_id","$params.group_id")
					.putKv("friends", "$callScripter_did");
			Boolean isSuccess = scriptingService.registerScript(FIND_NAME,
					new Condition("verify_user_permission", "queryHasResults",
							new ScriptFindBody("test_group", filter)),
					new Executable(FIND_NAME, Executable.TYPE_FIND,
							new ScriptFindBody("test_group", filter)),
					false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(2)
	void testRegisterScriptFindWithoutCondition() {
		try {
			Boolean isSuccess = scriptingService.registerScript(FIND_NO_CONDITION_NAME,
					new Executable("get_groups", Executable.TYPE_FIND,
							new ScriptFindBody("groups",
									new KeyValueDict().putKv("friends","$caller_did"))),
					false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(3)
	void testCallScriptFindWithoutCondition() {
		//TODO: A bug on node did_scripting.py line 121: return col maybe None.
		try {
			String result = scriptingService.callScript(FIND_NO_CONDITION_NAME,
					null, "appId", String.class).get();
			Assertions.assertNotNull(result);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(4)
	void testCallScriptUrlFindWithoutCondition() {
		//TODO: A bug on node did_scripting.py line 121: return col maybe None.
		try {
			String result = scriptingService.callScriptUrl(UPLOAD_FILE_NAME,
					null, "appId", String.class).get();
			Assertions.assertNotNull(result);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(5)
	void testUploadFile() {
		registerScriptFileUpload(UPLOAD_FILE_NAME);
		String transactionId = callScriptFileUpload(UPLOAD_FILE_NAME, fileName);
		uploadFileByTransActionId(transactionId);
		FilesServiceTest.verifyRemoteFileExists(filesService, fileName);
	}

	private void registerScriptFileUpload(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFileUploadExecutable(scriptName),
					false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private String callScriptFileUpload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileUploadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("transaction_id"));
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
		return null;
	}

	private void uploadFileByTransActionId(String transactionId) {
		try (Writer writer = scriptingService.uploadFile(transactionId, Writer.class).get();
			 FileReader fileReader = new FileReader(localSrcFilePath)) {
			Assertions.assertNotNull(writer);
			char[] buffer = new char[1];
			while (fileReader.read(buffer) != -1) {
				writer.write(buffer);
			}
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(6)
	void testFileDownload() {
		FilesServiceTest.removeLocalFile(localDstFilePath);
		registerScriptFileDownload(DOWNLOAD_FILE_NAME);
		String transactionId = callScriptFileDownload(DOWNLOAD_FILE_NAME, fileName);
		downloadFileByTransActionId(transactionId);
		Assertions.assertTrue(FilesServiceTest.isFileContentEqual(localSrcFilePath, localDstFilePath));
	}

	private void registerScriptFileDownload(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFileDownloadExecutable(scriptName),
					false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private String callScriptFileDownload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileDownloadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("transaction_id"));
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
		return null;
	}

	private void downloadFileByTransActionId(String transactionId) {
		try (Reader reader = scriptingService.downloadFile(transactionId, Reader.class).get()) {
			Assertions.assertNotNull(reader);
			Utils.cacheTextFile(reader, localDstFileRoot, fileName);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(7)
	void testFileProperties() {
		registerScriptFileProperties(FILE_PROPERTIES_NAME);
		callScriptFileProperties(FILE_PROPERTIES_NAME, fileName);
	}

	private void registerScriptFileProperties(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFilePropertiesExecutable(scriptName),
					false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private void callScriptFileProperties(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFilePropertiesParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("size"));
			Assertions.assertTrue(result.get(scriptName).get("size").asInt(0) > 0);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(8)
	void testFileHash() {
		registerScriptFileHash(FILE_HASH_NAME);
		callScriptFileHash(FILE_HASH_NAME, fileName);
	}

	private void registerScriptFileHash(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFileHashExecutable(scriptName),
					false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private void callScriptFileHash(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileHashParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("SHA256"));
			Assertions.assertFalse("".equals(result.get(scriptName).get("SHA256").asText("")));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(9)
	void testInsert() {
		registerScriptInsert(INSERT_NAME);
		callScriptInsert(INSERT_NAME);
	}

	private void registerScriptInsert(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createInsertExecutable(scriptName,
							new ScriptInsertExecutableBody(DATABASE_NAME, new KeyValueDict()
									.putKv("author", "$params.author")
									.putKv("content", "$params.content"),
									new KeyValueDict().putKv("bypass_document_validation",false).putKv("ordered",true)
								)), false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private void callScriptInsert(String scriptName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					"appId", JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("inserted_id"));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(10)
	void testUpdate() {
		registerScriptUpdate(UPDATE_NAME);
		callScriptUpdate(UPDATE_NAME);
	}

	private void registerScriptUpdate(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createUpdateExecutable(scriptName,
							new ScriptUpdateExecutableBody().setCollection(DATABASE_NAME)
								.setFilter(new KeyValueDict().putKv("author", "$params.author"))
								.setUpdate(new KeyValueDict().putKv("$set", new KeyValueDict()
										.putKv("author", "$params.author").putKv("content", "$params.content")))
								.setOptions(new KeyValueDict().putKv("bypass_document_validation",false)
										.putKv("upsert",true))
					), false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private void callScriptUpdate(String scriptName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					"appId", JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("upserted_id"));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(11)
	void testDelete() {
		registerScriptDelete(DELETE_NAME);
		callScriptDelete(DELETE_NAME);
	}

	private void registerScriptDelete(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createDeleteExecutable(scriptName,
							new ScriptDeleteExecutableBody().setCollection(DATABASE_NAME)
									.setFilter(new KeyValueDict().putKv("author", "$params.author"))
					), false, false).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	private void callScriptDelete(String scriptName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new KeyValueDict().putKv("author", "John")),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("deleted_count"));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * If exists, also return OK(_status).
	 */
	private static void create_test_database() {
		try {
			Boolean isSuccess = databaseService.createCollection(DATABASE_NAME, null).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * If not exists, also return OK(_status).
	 */
	private static void remove_test_database() {
		try {
			Boolean isSuccess = databaseService.deleteCollection(DATABASE_NAME).get();
			Assertions.assertTrue(isSuccess);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

}
