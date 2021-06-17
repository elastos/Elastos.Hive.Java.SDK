package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.elastos.hive.config.TestData;
import org.elastos.hive.connection.KeyValueDict;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.scripting.*;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

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

	private static final String COLLECTION_NAME = "script_database";

	private static String ownerDid;
	private static String appDid;

	private static FilesService filesService;
	private static DatabaseService databaseService;
	private static ScriptingService scriptingService;
	private static ScriptRunner scriptRunner;

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

	@BeforeAll public static void setUp() {
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			scriptingService = testData.newVault().getScriptingService();
			scriptRunner = testData.newScriptRunner();
			filesService = testData.newVault().getFilesService();
			databaseService = testData.newVault().getDatabaseService();
			ownerDid = testData.getOwnerDid();
			appDid = testData.getAppId();
		});
	}

	@AfterAll public static void tearDown() {
	}

	@Test @Order(1) void testInsert() {
		remove_test_database();
		create_test_database();
		registerScriptInsert(INSERT_NAME);
		callScriptInsert(INSERT_NAME);
	}

	private void registerScriptInsert(String scriptName) {
		Assertions.assertDoesNotThrow(() -> scriptingService.registerScript(scriptName,
				new InsertExecutable(scriptName, COLLECTION_NAME,
						new KeyValueDict().putKv("author", "$params.author").putKv("content", "$params.content"),
						new KeyValueDict().putKv("bypass_document_validation", false).putKv("ordered", true)),
				false, false).get());
	}

	private void callScriptInsert(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName,
					map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("inserted_id"));
		});
	}

	@Test @Order(2) void testFindWithoutCondition() {
		registerScriptFindWithoutCondition(FIND_NO_CONDITION_NAME);
		callScriptFindWithoutCondition(FIND_NO_CONDITION_NAME);
	}

	private void registerScriptFindWithoutCondition(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			scriptingService.registerScript(scriptName,
					new FindExecutable(scriptName, COLLECTION_NAME, new KeyValueDict().putKv("author","John")).setOutput(true),
					false, false).get();
		});
	}

	private void callScriptFindWithoutCondition(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertNotNull(
				scriptRunner.callScriptUrl(scriptName, "{}", ownerDid, appDid, String.class).get()));
	}

	@Test @Order(3) void testFind() {
		registerScriptFind(FIND_NAME);
		callScriptFind(FIND_NAME);
	}

	private void registerScriptFind(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			KeyValueDict filter = new KeyValueDict().putKv("author","John");
			scriptingService.registerScript(scriptName,
					new QueryHasResultCondition("verify_user_permission",COLLECTION_NAME, filter),
					new FindExecutable(scriptName, COLLECTION_NAME, filter).setOutput(true),
					false, false).get();
		});
	}

	private void callScriptFind(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			Assertions.assertNotNull(
				scriptRunner.callScript(scriptName, null, ownerDid, appDid, String.class).get());
		});
	}

	@Test @Order(4) void testUpdate() {
		registerScriptUpdate(UPDATE_NAME);
		callScriptUpdate(UPDATE_NAME);
	}

	private void registerScriptUpdate(String scriptName) {
		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new UpdateExecutable(scriptName, COLLECTION_NAME,
								new KeyValueDict().putKv("author", "$params.author"),
								new KeyValueDict().putKv("$set", new KeyValueDict().putKv("author", "$params.author")
										.putKv("content", "$params.content")),
								new KeyValueDict().putKv("bypass_document_validation", false).putKv("upsert", true)),
						false, false).get());
	}

	private void callScriptUpdate(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName,
					map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("upserted_id"));
		});
	}

	@Test @Order(5) void testDelete() {
		registerScriptDelete(DELETE_NAME);
		callScriptDelete(DELETE_NAME);
	}

	private void registerScriptDelete(String scriptName) {
		Assertions.assertDoesNotThrow(() -> {
			scriptingService.registerScript(scriptName,
					new DeleteExecutable(scriptName, COLLECTION_NAME,
							new KeyValueDict().putKv("author", "$params.author")),
					false, false).get();
		});
	}

	private void callScriptDelete(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(
					scriptName,

					map2JsonNode(new KeyValueDict().putKv("author", "John")),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("deleted_count"));
		});
	}

	@Test @Order(6) void testUploadFile() {
		registerScriptFileUpload(UPLOAD_FILE_NAME);
		String transactionId = callScriptFileUpload(UPLOAD_FILE_NAME, fileName);
		uploadFileByTransActionId(transactionId);
		FilesServiceTest.verifyRemoteFileExists(filesService, fileName);
	}

	private void registerScriptFileUpload(String scriptName) {
		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new FileUploadExecutable(scriptName).setOutput(true),
						false, false).get());
	}

	private String callScriptFileUpload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("transaction_id"));
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	private void uploadFileByTransActionId(String transactionId) {
		try (Writer writer = scriptRunner.uploadFile(transactionId, Writer.class).get();
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

	@Test @Order(7) void testFileDownload() {
		FilesServiceTest.removeLocalFile(localDstFilePath);
		registerScriptFileDownload(DOWNLOAD_FILE_NAME);
		String transactionId = callScriptFileDownload(DOWNLOAD_FILE_NAME, fileName);
		downloadFileByTransActionId(transactionId);
		Assertions.assertTrue(FilesServiceTest.isFileContentEqual(localSrcFilePath, localDstFilePath));
	}

	private void registerScriptFileDownload(String scriptName) {
		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new FileDownloadExecutable(scriptName).setOutput(true),
						false, false).get());
	}

	private String callScriptFileDownload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("transaction_id"));
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	private void downloadFileByTransActionId(String transactionId) {
		try (Reader reader = scriptRunner.downloadFile(transactionId, Reader.class).get()) {
			Assertions.assertNotNull(reader);
			Utils.cacheTextFile(reader, localDstFileRoot, fileName);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test @Order(8) void testFileProperties() {
		registerScriptFileProperties(FILE_PROPERTIES_NAME);
		callScriptFileProperties(FILE_PROPERTIES_NAME, fileName);
	}

	private void registerScriptFileProperties(String scriptName) {
		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new FilePropertiesExecutable(scriptName).setOutput(true),
						false, false).get());
	}

	private void callScriptFileProperties(String scriptName, String fileName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("size"));
			Assertions.assertTrue(result.get(scriptName).get("size").asInt(0) > 0);
		});
	}

	@Test @Order(9) void testFileHash() {
		registerScriptFileHash(FILE_HASH_NAME);
		callScriptFileHash(FILE_HASH_NAME, fileName);
		remove_test_database();
	}

	private void registerScriptFileHash(String scriptName) {
		Assertions.assertDoesNotThrow(()->
				scriptingService.registerScript(scriptName,
						new FileHashExecutable(scriptName).setOutput(true),
				false, false).get());
	}

	private void callScriptFileHash(String scriptName, String fileName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("SHA256"));
			Assertions.assertNotEquals(result.get(scriptName).get("SHA256").asText(""), "");
		});
	}

	/**
	 * If exists, also return OK(_status).
	 */
	private static void create_test_database() {
		try {
			databaseService.createCollection(COLLECTION_NAME).get();
		} catch (Exception e) {
			// pass
		}
	}

	/**
	 * If not exists, also return OK(_status).
	 */
	private static void remove_test_database() {
		try {
			databaseService.deleteCollection(COLLECTION_NAME).get();
		} catch (Exception e) {
			// pass
		}
	}

	private JsonNode map2JsonNode(Map<String, Object> map) {
		return new ObjectMapper().convertValue(map, JsonNode.class);
	}
}
