package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.scripting.AggregatedExecutable;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.HashExecutable;
import org.elastos.hive.scripting.PropertiesExecutable;
import org.elastos.hive.scripting.UploadExecutable;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class HiveURLTest {

	private final String scriptUrl = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/scripting/get_file_info?params={\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";

//	private final String scriptUrl = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/scripting/downloadScript?params={'key':'test'}";

	@Test
	public void testGetHiveURL() {
		CompletableFuture<Boolean> future = client.parseHiveURL(scriptUrl)
				.handleAsync((hiveURLInfo, throwable) -> (null != hiveURLInfo && throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void uploadFile() {
		Executable executable = new UploadExecutable("upload_file", "$params.path", true);
		CompletableFuture<Boolean> future = scriptingApi.registerScript("upload_file", executable, false, false)
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

					return scriptingApi.callScript(scriptName, params, "appId", JsonNode.class)
							.thenComposeAsync(jsonNode -> {
								String transactionId = jsonNode.get(scriptName).get("transaction_id").textValue();
								return scriptingApi.uploadFile(transactionId, Writer.class);
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
	public void testCallScriptUrl() {

		HashExecutable hashExecutable = new HashExecutable("file_hash", "$params.path");
		PropertiesExecutable propertiesExecutable = new PropertiesExecutable("file_properties", "$params.path");
		AggregatedExecutable executable = new AggregatedExecutable("file_properties_and_hash", new Executable[]{hashExecutable, propertiesExecutable});
		CompletableFuture<Boolean> fileInfoFuture = scriptingApi.registerScript("get_file_info", executable, false, false)
				.thenComposeAsync(aBoolean -> client.callScriptUrl(scriptUrl, String.class))
				.handle((success, ex) -> {
					if(ex != null) ex.printStackTrace();
					return (ex == null);
				});

		try {
			assertTrue(fileInfoFuture.get());
			assertTrue(fileInfoFuture.isCompletedExceptionally() == false);
			assertTrue(fileInfoFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static Client client;
	private static Scripting scriptingApi;
	private static String textLocalPath;

	@BeforeClass
	public static void setUp() {
		try {
			TestData testData = TestData.getInstance();
			client = testData.getClient();
			scriptingApi = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getScripting()).join();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/local/";
		textLocalPath = localRootPath + "test.txt";
	}

}
