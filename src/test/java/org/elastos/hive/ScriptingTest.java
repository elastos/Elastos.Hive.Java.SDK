package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.database.Date;
import org.elastos.hive.database.MaxKey;
import org.elastos.hive.database.MinKey;
import org.elastos.hive.database.ObjectId;
import org.elastos.hive.database.RegularExpression;
import org.elastos.hive.database.Timestamp;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.scripting.AggregatedExecutable;
import org.elastos.hive.scripting.AndCondition;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.DbFindQuery;
import org.elastos.hive.scripting.DbInsertQuery;
import org.elastos.hive.scripting.DownloadExecutable;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.HashExecutable;
import org.elastos.hive.scripting.OrCondition;
import org.elastos.hive.scripting.PropertiesExecutable;
import org.elastos.hive.scripting.QueryHasResultsCondition;
import org.elastos.hive.scripting.RawCondition;
import org.elastos.hive.scripting.RawExecutable;
import org.elastos.hive.scripting.UploadExecutable;
import org.elastos.hive.utils.JsonUtil;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptingTest {

	@Test
	public void test01_condition() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

		ObjectNode n = (ObjectNode) mapper.readTree(json);
		n.putPOJO("dateField", new Date());
		n.putPOJO("idField", new ObjectId("123123123123123123"));
		n.putPOJO("minKeyField", new MinKey(100));
		n.putPOJO("maxKeyField", new MaxKey(200));
		n.putPOJO("regexField", new RegularExpression("testpattern", "i"));
		n.putPOJO("tsField", new Timestamp(100000, 1234));

		Condition cond1 = new QueryHasResultsCondition("cond1", "c1", n);
		Condition cond2 = new QueryHasResultsCondition("cond2", "c2", n);
		Condition cond3 = new QueryHasResultsCondition("cond3", "c3", n);
		Condition cond4 = new QueryHasResultsCondition("cond4", "c4", n);
		Condition cond5 = new RawCondition(json);

		OrCondition orCond = new OrCondition("abc", new Condition[]{cond1, cond2});
		AndCondition andCond = new AndCondition("xyz", new Condition[]{cond3, cond4});

		OrCondition cond = new OrCondition("root");
		cond.append(orCond).append(cond5).append(andCond);

	}

	@Test
	public void test02_executable() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

		JsonNode n = mapper.readTree(json);

		Executable exec1 = new DbFindQuery("exec1", "c1", n);
		Executable exec2 = new DbFindQuery("exec2", "c2", n);
		Executable exec3 = new DbInsertQuery("exec3", "c3", n);
		Executable exec4 = new RawExecutable(json);

		AggregatedExecutable ae = new AggregatedExecutable("ae");
		ae.append(exec1).append(exec2).append(exec3);

		AggregatedExecutable ae2 = new AggregatedExecutable("ae2");
		ae2.append(exec1).append(exec2).append(ae).append(exec3);
	}

	@Test
	public void test03_registerScript() {

		CompletableFuture<Boolean> noConditionFuture;
		CompletableFuture<Boolean> withConditionFuture;

		{
			JsonNode filter = JsonUtil.deserialize("{\"friends\":\"$callScripter_did\"}");
			JsonNode options = JsonUtil.deserialize("{\"projection\":{\"_id\":false,\"name\":true}}");
			Executable executable = new DbFindQuery("get_groups", "groups", filter, options);
			noConditionFuture = scripting.registerScript(noConditionName, executable, false, false)
					.handle((success, ex) -> (ex == null));
		}

		{
			JsonNode filter = JsonUtil.deserialize("{\"_id\":\"$params.group_id\",\"friends\":\"$callScripter_did\"}");
			Executable executable = new DbFindQuery("get_groups", "test_group", filter);
			Condition condition = new QueryHasResultsCondition("verify_user_permission", "test_group", filter);
			withConditionFuture = scripting.registerScript(withConditionName, condition, executable, false, false)
					.handle((success, ex) -> (ex == null));
		}

		CompletableFuture allFuture = CompletableFuture.allOf(noConditionFuture, withConditionFuture);

		try {
			allFuture.get();
			assertTrue(allFuture.isCompletedExceptionally() == false);
			assertTrue(allFuture.isDone());
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void test04_callScript() {
		CompletableFuture<Boolean> stringFuture = scripting.callScript(noConditionName, null, "appId", String.class)
				.handle((success, ex) -> (ex == null));

		CompletableFuture<Boolean> byteFuture = scripting.callScript(noConditionName, null, "appId", byte[].class)
				.handle((success, ex) -> (ex == null));

		CompletableFuture<Boolean> jsonNodeFuture = scripting.callScript(noConditionName, null, "appId", JsonNode.class)
				.handle((success, ex) -> (ex == null));

		CompletableFuture<Boolean> readerFuture = scripting.callScript(noConditionName, null, "appId", Reader.class)
				.handle((success, ex) -> (ex == null));

		CompletableFuture allFuture = CompletableFuture.allOf(stringFuture, byteFuture, jsonNodeFuture, readerFuture);

		try {
			allFuture.get();
			assertTrue(allFuture.isCompletedExceptionally() == false);
			assertTrue(allFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test5_setUploadFile() {
		Executable executable = new UploadExecutable("upload_file", "$params.path", true);
		CompletableFuture<Boolean> future = scripting.registerScript("upload_file", executable, false, false)
				.thenComposeAsync(aBoolean -> {

					String scriptName = "upload_file";
					String metadata = "{\"group_id\":{\"$oid\":\"5f8d9dfe2f4c8b7a6f8ec0f1\"},\"path\":\"test.txt\"}";
					JsonNode params = null;
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						params = objectMapper.readTree(metadata);
					} catch (Exception e) {
						fail();
					}

					return scripting.callScript(scriptName, params, "appId", JsonNode.class)
							.thenComposeAsync(jsonNode -> {
								String transactionId = jsonNode.get(scriptName).get("transaction_id").textValue();
								return scripting.uploadFile(transactionId, Writer.class);
							}).handle((writer, ex) -> {
								if(null != writer) {
									Utils.fileWrite(textLocalPath, writer);
									try {
										writer.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								return ex == null;
							});
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void test6_setDownloadFile() {
		Executable executable = new DownloadExecutable("download_file", "$params.path", true);
		CompletableFuture<Boolean> downloadFuture = scripting.registerScript("download_file", executable, false, false)
				.thenComposeAsync(aBoolean -> {
					String scriptName = "download_file";
					String path = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";
					JsonNode params = JsonUtil.deserialize(path);

					return scripting.callScript(scriptName, params, "appId", JsonNode.class)
							.handle((jsonNode, ex) -> {
								String transactionId = jsonNode.get(scriptName).get("transaction_id").textValue();
								scripting.downloadFile(transactionId, Reader.class)
										.handle((reader, throwable) -> {
											if (throwable == null) {
												Utils.cacheTextFile(reader, testLocalCacheRootPath, "test.txt");
											}
											return throwable == null;
										});
								return true;
							});
				});

		try {
			assertTrue(downloadFuture.get());
			assertTrue(downloadFuture.isCompletedExceptionally() == false);
			assertTrue(downloadFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test7_setGetFileInfo() {
		HashExecutable hashExecutable = new HashExecutable("file_hash", "$params.path");
		PropertiesExecutable propertiesExecutable = new PropertiesExecutable("file_properties", "$params.path");
		AggregatedExecutable executable = new AggregatedExecutable("file_properties_and_hash", new Executable[]{hashExecutable, propertiesExecutable});
		CompletableFuture<Boolean> fileInfoFuture = scripting.registerScript("get_file_info", executable, false, false)
				.thenComposeAsync(aBoolean -> {
					JsonNode params = null;
					try {
						String executable1 = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";
						ObjectMapper objectMapper = new ObjectMapper();
						params = objectMapper.readTree(executable1);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return scripting.callScript("get_file_info", params, "appId", String.class);
				}).handle((success, ex) -> (ex == null));

		try {
			assertTrue(fileInfoFuture.get());
			assertTrue(fileInfoFuture.isCompletedExceptionally() == false);
			assertTrue(fileInfoFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void test8_callScriptUrl() {
		HashExecutable hashExecutable = new HashExecutable("file_hash", "$params.path");
		PropertiesExecutable propertiesExecutable = new PropertiesExecutable("file_properties", "$params.path");
		AggregatedExecutable executable = new AggregatedExecutable("file_properties_and_hash", new Executable[]{hashExecutable, propertiesExecutable});
		CompletableFuture<Boolean> fileInfoFuture = scripting.registerScript("get_file_info", executable, false, false)
				.thenComposeAsync(aBoolean -> {
					String params = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";
					return scripting.callScriptUrl("get_file_info", params, "appId", String.class);
				}).handle((success, ex) -> (ex == null));

		try {
			assertTrue(fileInfoFuture.get());
			assertTrue(fileInfoFuture.isCompletedExceptionally() == false);
			assertTrue(fileInfoFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = AppInstanceFactory.configSelector().getVault();
		scripting = vault.getScripting();
	}

	private final String textLocalPath;
	private final String testLocalCacheRootPath;

	private String noConditionName = "get_groups";
	private String withConditionName = "get_group_messages";

	private static Scripting scripting;

	public ScriptingTest() {
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/";
		textLocalPath = localRootPath + "test.txt";
		testLocalCacheRootPath = localRootPath + "cache/script/";
	}
}
