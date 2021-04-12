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

	//for other app access script function.
	private static final String APP_ID_OTHER = "appIdOther";
	//cross.user.did
	private static final String USER_ID_OTHER = "did:elastos:ip1e2eAY6Kw6shCbZhzksy9pYwLjTRFEU7";

	private static final String DATABASE_NAME = "script_database";

	private static ScriptingService scriptingService;
	private static FilesService filesService;
	private static DatabaseService databaseService;
	private static String appId;

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
			scriptingService = testData.newVault4Scripting().getScriptingService();
			filesService = testData.newVault().getFilesService();
			databaseService = testData.newVault().getDatabaseService();
			appId = testData.getAppId();
		});
		create_test_database();
	}

	@AfterAll public static void tearDown() {
		remove_test_database();
	}

	@Test @Order(1) void testInsert() {
		registerScriptInsert(INSERT_NAME);
		callScriptInsert(INSERT_NAME);
	}

	private void registerScriptInsert(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(scriptingService.registerScript(scriptName,
				Executable.createInsertExecutable(scriptName,
						new ScriptInsertExecutableBody(DATABASE_NAME, new KeyValueDict()
								.putKv("author", "$params.author")
								.putKv("content", "$params.content"),
								new KeyValueDict().putKv("bypass_document_validation",false).putKv("ordered",true)
						)), false, false).get()));
	}

	private void callScriptInsert(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					appId, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("inserted_id"));
		});
	}

	@Test @Order(2) void testRegisterScriptFind() {
		Assertions.assertDoesNotThrow(()->{
			KeyValueDict filter = new KeyValueDict().putKv("author","John");
			Assertions.assertTrue(scriptingService.registerScript(FIND_NAME,
					new Condition("verify_user_permission", "queryHasResults",
							new ScriptFindBody(DATABASE_NAME, filter)),
					new Executable(FIND_NAME, Executable.TYPE_FIND,
							new ScriptFindBody(DATABASE_NAME, filter)),
					false, false).get());
		});
	}

	@Test @Order(3) void testRegisterScriptFindWithoutCondition() {
		Assertions.assertDoesNotThrow(()->{
			Assertions.assertTrue(scriptingService.registerScript(FIND_NO_CONDITION_NAME,
					new Executable("get_groups", Executable.TYPE_FIND,
							new ScriptFindBody("groups",
									new KeyValueDict().putKv("friends","$caller_did"))),
					false, false).get());
		});
	}

	@Test @Order(4) void testCallScriptFind() {
		Assertions.assertDoesNotThrow(()->Assertions.assertNotNull(
				scriptingService.callScript(FIND_NAME,
				null, appId, String.class).get()));
	}

	@Test @Order(5) void testCallScriptUrlFindWithoutCondition() {
		Assertions.assertDoesNotThrow(()->Assertions.assertNotNull(
				scriptingService.callScriptUrl(UPLOAD_FILE_NAME,
				null, appId, String.class).get()));
	}

	@Test @Order(6) void testUpdate() {
		registerScriptUpdate(UPDATE_NAME);
		callScriptUpdate(UPDATE_NAME);
	}

	private void registerScriptUpdate(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				scriptingService.registerScript(scriptName,
						Executable.createUpdateExecutable(scriptName,
								new ScriptUpdateExecutableBody().setCollection(DATABASE_NAME)
										.setFilter(new KeyValueDict().putKv("author", "$params.author"))
										.setUpdate(new KeyValueDict().putKv("$set", new KeyValueDict()
												.putKv("author", "$params.author").putKv("content", "$params.content")))
										.setOptions(new KeyValueDict().putKv("bypass_document_validation",false)
												.putKv("upsert",true))
						), false, false).get()));
	}

	private void callScriptUpdate(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					appId, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("upserted_id"));
		});
	}

	@Test @Order(7) void testDelete() {
		registerScriptDelete(DELETE_NAME);
		callScriptDelete(DELETE_NAME);
	}

	private void registerScriptDelete(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createDeleteExecutable(scriptName,
							new ScriptDeleteExecutableBody().setCollection(DATABASE_NAME)
									.setFilter(new KeyValueDict().putKv("author", "$params.author"))
					), false, false).get();
			Assertions.assertTrue(isSuccess);
		});
	}

	private void callScriptDelete(String scriptName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new KeyValueDict().putKv("author", "John")),
					appId, JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("deleted_count"));
		});
	}

	@Test @Order(8) void testUploadFile() {
		registerScriptFileUpload(UPLOAD_FILE_NAME);
		String transactionId = callScriptFileUpload(UPLOAD_FILE_NAME, fileName);
		uploadFileByTransActionId(transactionId);
		FilesServiceTest.verifyRemoteFileExists(filesService, fileName);
	}

	private void registerScriptFileUpload(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				scriptingService.registerScript(scriptName,
				Executable.createFileUploadExecutable(scriptName),
				false, false).get()));
	}

	private String callScriptFileUpload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileUploadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					appId, JsonNode.class).get();
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

	@Test @Order(9) void testFileDownload() {
		FilesServiceTest.removeLocalFile(localDstFilePath);
		registerScriptFileDownload(DOWNLOAD_FILE_NAME);
		String transactionId = callScriptFileDownload(DOWNLOAD_FILE_NAME, fileName);
		downloadFileByTransActionId(transactionId);
		Assertions.assertTrue(FilesServiceTest.isFileContentEqual(localSrcFilePath, localDstFilePath));
	}

	private void registerScriptFileDownload(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				scriptingService.registerScript(scriptName,
				Executable.createFileDownloadExecutable(scriptName),
				false, false).get()));
	}

	private String callScriptFileDownload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileDownloadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					appId, JsonNode.class).get();
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
		try (Reader reader = scriptingService.downloadFile(transactionId, Reader.class).get()) {
			Assertions.assertNotNull(reader);
			Utils.cacheTextFile(reader, localDstFileRoot, fileName);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test @Order(10) void testFileProperties() {
		registerScriptFileProperties(FILE_PROPERTIES_NAME);
		callScriptFileProperties(FILE_PROPERTIES_NAME, fileName);
	}

	private void registerScriptFileProperties(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				scriptingService.registerScript(scriptName,
				Executable.createFilePropertiesExecutable(scriptName),
				false, false).get()));
	}

	private void callScriptFileProperties(String scriptName, String fileName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFilePropertiesParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					appId, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("size"));
			Assertions.assertTrue(result.get(scriptName).get("size").asInt(0) > 0);
		});
	}

	@Test @Order(11) void testFileHash() {
		registerScriptFileHash(FILE_HASH_NAME);
		callScriptFileHash(FILE_HASH_NAME, fileName);
	}

	private void registerScriptFileHash(String scriptName) {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				scriptingService.registerScript(scriptName,
				Executable.createFileHashExecutable(scriptName),
				false, false).get()));
	}

	private void callScriptFileHash(String scriptName, String fileName) {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileHashParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					appId, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("SHA256"));
			Assertions.assertFalse("".equals(result.get(scriptName).get("SHA256").asText("")));
		});
	}



	/**
	 * If exists, also return OK(_status).
	 */
	private static void create_test_database() {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				databaseService.createCollection(DATABASE_NAME, null).get()));
	}

	/**
	 * If not exists, also return OK(_status).
	 */
	private static void remove_test_database() {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				databaseService.deleteCollection(DATABASE_NAME).get()));
	}

}
