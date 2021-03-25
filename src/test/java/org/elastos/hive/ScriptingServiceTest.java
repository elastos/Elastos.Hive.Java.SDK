package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.*;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;
import org.junit.AfterClass;
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

	@Test
	public void test01_registerScriptFind() {
		try {
			ScriptKvItem filter = new ScriptKvItem().putKv("_id","$params.group_id")
					.putKv("friends", "$callScripter_did");
			Boolean isSuccess = scriptingService.registerScript(FIND_NAME,
					new Condition("verify_user_permission", "queryHasResults",
							new ScriptFindBody("test_group", filter)),
					new Executable(FIND_NAME, Executable.TYPE_FIND,
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
			Boolean isSuccess = scriptingService.registerScript(FIND_NO_CONDITION_NAME,
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
			String result = scriptingService.callScript(FIND_NO_CONDITION_NAME,
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
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("transaction_id"));
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
					Executable.createFileDownloadParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("transaction_id"));
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

	@Test
	public void test07_fileProperties() {
		registerScriptFileProperties(FILE_PROPERTIES_NAME);
		callScriptFileProperties(FILE_PROPERTIES_NAME, fileName);
	}

	private void registerScriptFileProperties(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFilePropertiesExecutable(scriptName),
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

	private void callScriptFileProperties(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFilePropertiesParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("size"));
			assertTrue(result.get(scriptName).get("size").asInt(0) > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test08_fileHash() {
		registerScriptFileHash(FILE_HASH_NAME);
		callScriptFileHash(FILE_HASH_NAME, fileName);
	}

	private void registerScriptFileHash(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createFileHashExecutable(scriptName),
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

	private void callScriptFileHash(String scriptName, String fileName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					Executable.createFileHashParams("5f8d9dfe2f4c8b7a6f8ec0f1", fileName),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("SHA256"));
			assertFalse("".equals(result.get(scriptName).get("SHA256").asText("")));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test09_insert() {
		registerScriptInsert(INSERT_NAME);
		callScriptInsert(INSERT_NAME);
	}

	private void registerScriptInsert(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createInsertExecutable(scriptName,
							new ScriptInsertExecutableBody(DATABASE_NAME, new ScriptKvItem()
									.putKv("author", "$params.author")
									.putKv("content", "$params.content"),
									new ScriptKvItem().putKv("bypass_document_validation",false).putKv("ordered",true)
								)), false, false)
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

	private void callScriptInsert(String scriptName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new ScriptKvItem().putKv("author", "John").putKv("content", "message")),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("inserted_id"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test10_update() {
		registerScriptUpdate(UPDATE_NAME);
		callScriptUpdate(UPDATE_NAME);
	}

	private void registerScriptUpdate(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createUpdateExecutable(scriptName,
							new ScriptUpdateExecutableBody().setCollection(DATABASE_NAME)
								.setFilter(new ScriptKvItem().putKv("author", "$params.author"))
								.setUpdate(new ScriptKvItem().putKv("$set", new ScriptKvItem()
										.putKv("author", "$params.author").putKv("content", "$params.content")))
								.setOptions(new ScriptKvItem().putKv("bypass_document_validation",false)
										.putKv("upsert",true))
					), false, false)
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

	private void callScriptUpdate(String scriptName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new ScriptKvItem().putKv("author", "John").putKv("content", "message")),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("upserted_id"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test11_delete() {
		registerScriptDelete(DELETE_NAME);
		callScriptDelete(DELETE_NAME);
	}

	private void registerScriptDelete(String scriptName) {
		try {
			Boolean isSuccess = scriptingService.registerScript(scriptName,
					Executable.createDeleteExecutable(scriptName,
							new ScriptDeleteExecutableBody().setCollection(DATABASE_NAME)
									.setFilter(new ScriptKvItem().putKv("author", "$params.author"))
					), false, false)
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

	private void callScriptDelete(String scriptName) {
		try {
			JsonNode result = scriptingService.callScript(scriptName,
					HiveResponseBody.map2JsonNode(
							new ScriptKvItem().putKv("author", "John")),
					"appId", JsonNode.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
			assertTrue(result.has(scriptName));
			assertTrue(result.get(scriptName).has("deleted_count"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * If exists, also return OK(_status).
	 */
	private static void create_test_database() {
		try {
			Boolean isSuccess = databaseService.createCollection(DATABASE_NAME, null)
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

	/**
	 * If not exists, also return OK(_status).
	 */
	private static void remove_test_database() {
		try {
			Boolean isSuccess = databaseService.deleteCollection(DATABASE_NAME)
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

	@BeforeClass
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

	@AfterClass
	public static void tearDown() {
		remove_test_database();
	}

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
}
