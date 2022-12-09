package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.AlreadyExistsException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.scripting.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ExecutionException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScriptingServiceTest {
	private static final Logger log = LoggerFactory.getLogger(ScriptingServiceTest.class);

	private static final String FIND_NAME = "script_database_find";
	private static final String COUNT_NAME = "script_database_count";
	private static final String INSERT_NAME = "script_database_insert";
	private static final String UPDATE_NAME = "script_database_update";
	private static final String DELETE_NAME = "script_database_delete";
	private static final String UPLOAD_FILE = "script_upload_file";
	private static final String DOWNLOAD_FILE = "script_download_file";
	private static final String FILE_PROPERTIES = "script_file_properties";
	private static final String FILE_HASH = "script_file_hash";

	private static final String COLLECTION_NAME = "java_script_database";

	private static String targetDid;
	private static String appDid;

	private static VaultSubscription subscription;
	private static FilesService filesService;
	private static DatabaseService databaseService;
	private static ScriptingService scriptingService;
	private static ScriptRunner scriptRunner;
	private static ScriptRunner anonymousRunner;

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

	@BeforeAll public static void setUp() throws HiveException, DIDException, InterruptedException, ExecutionException {
		TestData testData = TestData.getInstance();
		Assertions.assertDoesNotThrow(()->{
			scriptingService = testData.newVault().getScriptingService();
			scriptRunner = testData.newScriptRunner();
			anonymousRunner = testData.newAnonymousScriptRunner();
			filesService = testData.newVault().getFilesService();
			databaseService = testData.newVault().getDatabaseService();
			targetDid = testData.getUserDid();
			appDid = testData.getAppDid();
		});

		// try to subscribe for script owner.
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
		try {
			subscription.subscribe().get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof AlreadyExistsException) {}
			else throw e;
		}

		// try to create a new collection.
		try {
			databaseService.createCollection(COLLECTION_NAME).get();
		} catch (Exception e) {
			if (e.getCause() instanceof AlreadyExistsException)
				log.info("Already exists, skip");
			else
				throw new RuntimeException("Failed to create collection: " + e.getMessage());
		}
	}

	@AfterAll public static void tearDown() {
		try {
			databaseService.deleteCollection(COLLECTION_NAME).get();
		} catch (Exception e) {
			if (e.getCause() instanceof NotFoundException)
				log.info("Already deleted, skip");
			else
				throw new RuntimeException("Failed to delete collection: " + e.getMessage());
		}
	}

	private void insertDocument(String scriptName, String executableName, String message, int count) {
		ObjectNode params = JsonNodeFactory.instance.objectNode();
		params.put("author","John");
		params.put("content", message);
		params.put("words_count", count);
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName, params,
					targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("inserted_id"));
			Assertions.assertNotNull(result.get(executableName).get("inserted_id").toString());
		});
	}

	@Test @Order(1) void testInsert() {
		String scriptName = INSERT_NAME;
		String executableName = "database_insert";

		ObjectNode doc = JsonNodeFactory.instance.objectNode();
		doc.put("author", "$params.author");
		doc.put("content", "$params.content");
		doc.put("words_count", "$params.words_count");

		ObjectNode options = JsonNodeFactory.instance.objectNode();
		options.put("bypass_document_validation", false);
		options.put("ordered", true);

		Assertions.assertDoesNotThrow(() -> scriptingService.registerScript(scriptName,
				new InsertExecutable(executableName, COLLECTION_NAME, doc, null),
				false, false).get());

		this.insertDocument(scriptName, executableName, "message1", 10000);
		this.insertDocument(scriptName, executableName, "message2", 20000);
		this.insertDocument(scriptName, executableName, "message3", 30000);
		this.insertDocument(scriptName, executableName, "message4", 40000);
		this.insertDocument(scriptName, executableName, "message5", 50000);
		this.insertDocument(scriptName, executableName, "message6", 60000);

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	private void testCountInternal(boolean anonymous) {
		String scriptName = COUNT_NAME;
		String executableName = "database_count";
		ScriptRunner runner = anonymous ? anonymousRunner : scriptRunner;

		ObjectNode filter = JsonNodeFactory.instance.objectNode();
		filter.put("author","John");
		Assertions.assertDoesNotThrow(()->{
			scriptingService.registerScript(scriptName,
					new QueryHasResultCondition("verify_user_permission",COLLECTION_NAME, filter),
					new CountExecutable(executableName, COLLECTION_NAME, filter),
					anonymous, anonymous).get();
		});

		Assertions.assertDoesNotThrow(()-> {
			List<ScriptContent> scripts = scriptingService.getScripts(null, 0, 0).get();
			Assertions.assertFalse(scripts.isEmpty());
		});

		Assertions.assertDoesNotThrow(()->{
			JsonNode result = runner.callScript(scriptName, null, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("count"));
			Assertions.assertEquals(result.get(executableName).get("count").asInt(), 6);
		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	@Test @Order(2) void testCount() {
		this.testCountInternal(false);
		this.testCountInternal(true);
	}

	@Test @Order(2) void testFindWithoutCondition() {
		String scriptName = FIND_NAME;
		String executableName = "database_find";

		ObjectNode filter = JsonNodeFactory.instance.objectNode();
		filter.put("author","$params.author");
		ObjectNode wordsCount = JsonNodeFactory.instance.objectNode();
		wordsCount.put("$gt","$params.start");
		wordsCount.put("$lt","$params.end");
		filter.put("words_count", wordsCount);
		Assertions.assertDoesNotThrow(()->{
			scriptingService.registerScript(scriptName,
					new FindExecutable(executableName, COLLECTION_NAME, filter),
					false, false).get();
		});

		ObjectNode params = JsonNodeFactory.instance.objectNode();
		params.put("author","John");
		params.put("start", 5000);
		params.put("end", 15000);
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(
					scriptName, params, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("total"));
			Assertions.assertEquals(result.get(executableName).get("total").asInt(), 1);
			Assertions.assertTrue(result.get(executableName).has("items"));
			Assertions.assertEquals(result.get(executableName).get("items").size(), 1);
		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	private void testFindInternal(boolean anonymous) {
		String scriptName = FIND_NAME;
		String executableName = "database_find";
		ScriptRunner runner = anonymous ? anonymousRunner : scriptRunner;

		ObjectNode filter = JsonNodeFactory.instance.objectNode();
		filter.put("author","John");
		Assertions.assertDoesNotThrow(()->{
			scriptingService.registerScript(scriptName,
					new QueryHasResultCondition("verify_user_permission",COLLECTION_NAME, filter),
					new FindExecutable(executableName, COLLECTION_NAME, filter),
					anonymous, anonymous).get();
		});

		Assertions.assertDoesNotThrow(()->{
			JsonNode result = runner.callScript(scriptName, null, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("total"));
			Assertions.assertTrue(result.get(executableName).get("total").asInt() > 1);
			Assertions.assertTrue(result.get(executableName).has("items"));
			Assertions.assertTrue(result.get(executableName).get("items").size() > 1);
		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	@Test @Order(3) void testFind() {
		this.testFindInternal(false);
		this.testFindInternal(true);
	}

	private void executeUpdate(String scriptName, String executableName, int words_count) {
		ObjectNode params = JsonNodeFactory.instance.objectNode();
		params.put("content", "message5");
		params.put("words_count", words_count);
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName, params, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("upserted_id"));
			Assertions.assertNotNull(result.get(executableName).get("upserted_id"));
		});
	}

	@Test @Order(4) void testUpdate() {
		String scriptName = UPDATE_NAME;
		String executableName = "database_update";

		ObjectNode filter = JsonNodeFactory.instance.objectNode();
		filter.put("content", "$params.content");
		ObjectNode set = JsonNodeFactory.instance.objectNode();
		set.put("words_count", "$params.words_count");
		ObjectNode update = JsonNodeFactory.instance.objectNode();
		update.put("$set", set);
		ObjectNode options = JsonNodeFactory.instance.objectNode();
		options.put("bypass_document_validation", false);
		options.put("upsert", false);
		Assertions.assertDoesNotThrow(() -> {
			scriptingService.registerScript(scriptName,
					new UpdateExecutable(executableName, COLLECTION_NAME, filter, update, options),
					false, false).get();
		});

		this.executeUpdate(scriptName, executableName, 60000);
		this.executeUpdate(scriptName, executableName, 50000);

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	@Test @Order(5) void testDelete() {
		String scriptName = DELETE_NAME;
		String executableName = "database_delete";

		ObjectNode filter = JsonNodeFactory.instance.objectNode();
		filter.put("author", "$params.author");
		filter.put("content", "$params.content");
		Assertions.assertDoesNotThrow(() -> {
			scriptingService.registerScript(scriptName,
					new DeleteExecutable(executableName, COLLECTION_NAME, filter),
					false, false).get();
		});

		ObjectNode params = JsonNodeFactory.instance.objectNode();
		params.put("author", "John");
		params.put("content", "message5");
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName, params, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("deleted_count"));
			Assertions.assertEquals(result.get(executableName).get("deleted_count").asInt(), 1);
		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	@Test @Order(6) void testUploadFile() {
		String scriptName = UPLOAD_FILE;
		String executableName = "file_upload";

		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new FileUploadExecutable(executableName),
						false, false).get());

		Assertions.assertDoesNotThrow(() -> {
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("transaction_id"));
			Assertions.assertNotNull(result.get(executableName).get("transaction_id"));
			String transactionId = result.get(executableName).get("transaction_id").textValue();

			// really upload file
			try (Writer writer = scriptRunner.uploadFile(transactionId, Writer.class).get();
				 FileReader fileReader = new FileReader(localSrcFilePath)) {
				Assertions.assertNotNull(writer);
				char[] buffer = new char[1];
				while (fileReader.read(buffer) != -1) {
					writer.write(buffer);
				}
				writer.flush();
			} catch (Exception e) {
				Assertions.fail(Throwables.getStackTraceAsString(e));
			}
		});

		FilesServiceTest.verifyRemoteFileExists(filesService, fileName);
		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	private void testDownloadFile(boolean anonymous) {
		String scriptName = DOWNLOAD_FILE;
		String executableName = "file_download";

		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new FileDownloadExecutable(executableName).setOutput(true),
						anonymous, anonymous).get());

		Assertions.assertDoesNotThrow(() -> {
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("transaction_id"));
			String transactionId = result.get(executableName).get("transaction_id").textValue();

			// download by transaction id
			try (Reader reader = scriptRunner.downloadFile(transactionId, Reader.class).get()) {
				Assertions.assertNotNull(reader);
				Utils.cacheTextFile(reader, localDstFileRoot, fileName);
			} catch (Exception e) {
				Assertions.fail(Throwables.getStackTraceAsString(e));
			}

		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
		Assertions.assertTrue(FilesServiceTest.isFileContentEqual(localSrcFilePath, localDstFilePath));
	}

	@Test @Order(7) void testFileDownload() {
        FilesServiceTest.removeLocalFile(localDstFilePath);

		testDownloadFile(false);
		testDownloadFile(true);
	}

	@Test @Order(8) void testFileProperties() {
		String scriptName = FILE_PROPERTIES;
		String executableName = "file_properties";

		Assertions.assertDoesNotThrow(() ->
				scriptingService.registerScript(scriptName,
						new FilePropertiesExecutable(executableName).setOutput(true),
						false, false).get());

		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("size"));
			Assertions.assertTrue(result.get(executableName).get("size").asInt() > 0);
		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	@Test @Order(9) void testFileHash() {
		String scriptName = FILE_HASH;
		String executableName = "file_hash";

		Assertions.assertDoesNotThrow(()->
				scriptingService.registerScript(scriptName,
						new FileHashExecutable(executableName).setOutput(true),
				false, false).get());

		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(executableName));
			Assertions.assertTrue(result.get(executableName).has("SHA256"));
			Assertions.assertNotEquals(result.get(executableName).get("SHA256").asText(""), "");
		});

		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}

	private String callScriptFileDownload(String scriptName, String fileName) {
		try {
			JsonNode result = scriptRunner.callScript(scriptName,
					Executable.createRunFileParams(fileName),
					targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(scriptName));
			Assertions.assertTrue(result.get(scriptName).has("transaction_id"));
			if (result.get(scriptName).has("anonymous_url")) {
				log.info("anonymous_url: " + result.get(scriptName).get("anonymous_url").textValue());
			}
			return result.get(scriptName).get("transaction_id").textValue();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	/**
	 * for files service
	 */
	public void downloadPublicTxtFileAndVerify(String scriptName, String cacheRoot, String cacheFileName, String checkFilePath) {
		String transId = this.callScriptFileDownload(scriptName, null);
		try (Reader reader = scriptRunner.downloadFile(transId, Reader.class).get()) {
			Assertions.assertNotNull(reader);
			Utils.cacheTextFile(reader, cacheRoot, cacheFileName);
			Assertions.assertTrue(FilesServiceTest.isFileContentEqual(checkFilePath, cacheRoot + cacheFileName));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * for files service
	 */
	public void downloadPublicBinFileAndVerify(String scriptName,
											   String remotePath,
											   String cacheRoot,
											   String cacheFileName,
											   String checkFilePath) {
		String transId = this.callScriptFileDownload(scriptName, remotePath);
		try (InputStream in = scriptRunner.downloadFile(transId, InputStream.class).get()) {
			Assertions.assertNotNull(in);
			Utils.cacheBinFile(in, cacheRoot, cacheFileName);
			Assertions.assertTrue(FilesServiceTest.isFileContentEqual(checkFilePath, cacheRoot + cacheFileName));
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	/**
	 * for files service
	 */
	public void unregisterScript(String scriptName) {
		Assertions.assertDoesNotThrow(()->{ scriptingService.unregisterScript(scriptName).get(); });
	}
}
