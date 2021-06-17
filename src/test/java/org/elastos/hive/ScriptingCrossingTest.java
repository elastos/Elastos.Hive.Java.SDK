package org.elastos.hive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

import org.elastos.hive.config.TestData;
import org.elastos.hive.connection.KeyValueDict;
import org.elastos.hive.vault.database.InsertOptions;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.scripting.*;
import org.junit.jupiter.api.*;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScriptingCrossingTest {

	private static final String COLLECTION_GROUP = "st_group";
	private static final String COLLECTION_GROUP_MESSAGE = "st_group_message";
	private static final String SCRIPT_NAME = "get_group_message";

	private static ScriptingService scriptingService;
	private static ScriptRunner scriptRunner;
	private static DatabaseService databaseService;

	private static String ownerDid;
	private static String callDid;
	private static String appDid;

	@BeforeAll
	public static void setUp() {
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			scriptingService = testData.newVault().getScriptingService();
			scriptRunner = testData.newCallerScriptRunner();
			databaseService = testData.newVault().getDatabaseService();
			ownerDid = testData.getOwnerDid();
			appDid = testData.getAppId();
			callDid = testData.getCallerDid();
		});
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
			KeyValueDict filter = new KeyValueDict().putKv("collection", COLLECTION_GROUP_MESSAGE)
					.putKv("did", "$caller_did");
			scriptingService.registerScript(SCRIPT_NAME,
					new QueryHasResultCondition("verify_user_permission", COLLECTION_GROUP, filter),
					new InsertExecutable(SCRIPT_NAME, COLLECTION_GROUP_MESSAGE,
							new KeyValueDict().putKv("author", "$params.author").putKv("content", "$params.content"),
							new KeyValueDict().putKv("bypass_document_validation",false).putKv("ordered",true)),
					false, false).get();
		});
	}

	private void run_script_with_group_permission() {
		Assertions.assertDoesNotThrow(()->{
			JsonNode result = scriptRunner.callScript(SCRIPT_NAME,
					map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					ownerDid, appDid, JsonNode.class).get();
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
			JsonNode result = scriptRunner.callScript(SCRIPT_NAME,
					map2JsonNode(
							new KeyValueDict().putKv("author", "John").putKv("content", "message")),
					ownerDid, appDid, JsonNode.class).get();
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.has(SCRIPT_NAME));
			Assertions.assertTrue(result.get(SCRIPT_NAME).has("inserted_id"));
		});
	}

	private void uninit_for_caller() {
		databaseService.deleteCollection(COLLECTION_GROUP_MESSAGE);
		databaseService.deleteCollection(COLLECTION_GROUP);
	}

	private JsonNode map2JsonNode(Map<String, Object> map) {
		return new ObjectMapper().convertValue(map, JsonNode.class);
	}

}
