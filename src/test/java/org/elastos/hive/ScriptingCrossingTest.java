package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.vault.database.InsertOptions;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.scripting.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScriptingCrossingTest {

	private static final String COLLECTION_GROUP = "st_group";
	private static final String COLLECTION_GROUP_MESSAGE = "st_group_message";
	private static final String SCRIPT_NAME = "get_group_message";

	private static final String HIVE_URL_SCRIPT_NAME = "downloadFileWithHiveUrl";
	private static final String HIVE_URL_FILE_NAME = "hiveUrl.txt";
	private static final String HIVE_URL_FILE_CONTENT = "The file content on hiveUrl.txt";

	private static VaultSubscription subscription;
	private static ScriptingService scriptingService;
	private static ScriptRunner scriptRunner;
	private static DatabaseService databaseService;
	private static FilesService filesService;

	private static String targetDid;
	private static String callDid;
	private static String appDid;

	@BeforeAll
	public static void setUp() {
		trySubscribeVault();
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			Vault vault = testData.newVault();

			scriptingService = vault.getScriptingService();
			databaseService = vault.getDatabaseService();
			filesService = vault.getFilesService();
			scriptRunner = testData.newCallerScriptRunner();

			targetDid = testData.getUserDid();
			appDid = testData.getAppDid();
			callDid = testData.getCallerDid();
		});
	}

	private static void trySubscribeVault() {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
		try {
			subscription.subscribe();
		} catch (NotFoundException e) {}
	}

	/**
	 * This process shows how caller run script with/without group permission.
	 */
	@Test
	@Order(1) void testCallerGroupPermission() {
		init_for_caller();
		set_permission_for_caller();
		register_script_for_caller();
		run_script_with_group_permission();//called by caller.
		remove_permission_for_caller();
		run_script_without_group_permission();//called by caller.
		uninit_for_caller();
	}

	private void init_for_caller() {
		Assertions.assertDoesNotThrow(()->{
			databaseService.createCollection(COLLECTION_GROUP).get();
			databaseService.createCollection(COLLECTION_GROUP_MESSAGE).get();
		});
	}

	private void set_permission_for_caller() {
		Assertions.assertDoesNotThrow(()->{
			//add group named COLLECTION_GROUP_MESSAGE and add caller did into it,
			//  then caller will get the permission
			//  to access collection COLLECTION_GROUP_MESSAGE
			ObjectNode docNode = JsonNodeFactory.instance.objectNode();
			docNode.put("collection", COLLECTION_GROUP_MESSAGE);
			docNode.put("did", callDid);
			databaseService.insertOne(COLLECTION_GROUP, docNode,
					new InsertOptions().bypassDocumentValidation(false)).get();
		});
	}

	private void register_script_for_caller() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("collection", COLLECTION_GROUP_MESSAGE);
			filter.put("did", "$caller_did");
			ObjectNode doc = JsonNodeFactory.instance.objectNode();
			doc.put("author", "$params.author");
			doc.put("content", "$params.content");
			ObjectNode options = JsonNodeFactory.instance.objectNode();
			options.put("bypass_document_validation",false);
			options.put("ordered",true);
			scriptingService.registerScript(SCRIPT_NAME,
					new QueryHasResultCondition("verify_user_permission", COLLECTION_GROUP, filter),
					new InsertExecutable(SCRIPT_NAME, COLLECTION_GROUP_MESSAGE, doc, options),
					false, false).get();
		});
	}

	private void run_script_with_group_permission() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode params = JsonNodeFactory.instance.objectNode();
			params.put("author", "John");
			params.put("content", "message");
			JsonNode result = scriptRunner.callScript(SCRIPT_NAME, params, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(SCRIPT_NAME));
			Assertions.assertTrue(result.get(SCRIPT_NAME).has("inserted_id"));
		});
	}

	private void remove_permission_for_caller() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("collection", COLLECTION_GROUP_MESSAGE);
			filter.put("did", callDid);
			databaseService.deleteOne(COLLECTION_GROUP, filter);
		});
	}

	private void run_script_without_group_permission() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode params = JsonNodeFactory.instance.objectNode();
			params.put("author", "John");
			params.put("content", "message");
			JsonNode result = scriptRunner.callScript(SCRIPT_NAME, params, targetDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(SCRIPT_NAME));
			Assertions.assertTrue(result.get(SCRIPT_NAME).has("inserted_id"));
		});
	}

	private void uninit_for_caller() {
		databaseService.deleteCollection(COLLECTION_GROUP_MESSAGE);
		databaseService.deleteCollection(COLLECTION_GROUP);
	}

	@Test
	@Order(2) void testDownloadByHiveUrl() {
		String hiveUrl = String.format("hive://%s@%s/%s?params=%s",
				targetDid, appDid, HIVE_URL_SCRIPT_NAME, "{\"empty\":0}");
		Assertions.assertDoesNotThrow(this::uploadFile);
		Assertions.assertDoesNotThrow(() -> registerScript().get());
		Assertions.assertDoesNotThrow(() -> scriptRunner.downloadFileByHiveUrl(hiveUrl, Reader.class)
				.thenAccept(reader -> {
					Assertions.assertNotNull(reader);
					Assertions.assertEquals(HIVE_URL_FILE_CONTENT, getFileContentByReader(reader));
				}).get());
	}

	private String getFileContentByReader(Reader reader) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			char[] buffer = new char[1];
			while (reader.read(buffer) != -1) {
				stringBuilder.append(buffer);
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			throw new CompletionException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private CompletableFuture<Void> registerScript() {
		AggregatedExecutable executable = new AggregatedExecutable("downloadGroup");
		executable.appendExecutable(new FileDownloadExecutable("download", HIVE_URL_FILE_NAME));
		return scriptingService.registerScript(HIVE_URL_SCRIPT_NAME, executable, true, true);
	}

	private void uploadFile() throws IOException, ExecutionException, InterruptedException {
		try (Writer writer = filesService.getUploadWriter(HIVE_URL_FILE_NAME).get()) {
			Assertions.assertNotNull(writer);
			writer.write(HIVE_URL_FILE_CONTENT);
		}
	}

}
