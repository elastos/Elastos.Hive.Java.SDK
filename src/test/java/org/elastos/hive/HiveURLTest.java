package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.scripting.DownloadExecutable;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.UploadExecutable;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class HiveURLTest {

	private final String downloadUrl = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/scripting/download_file?params={\"path\":\"test.txt\"}";

	@Test
	public void testGetHiveURL() {
		CompletableFuture<Boolean> future = client.parseHiveURL(downloadUrl)
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
					String metadata = "{\"path\":\"test.txt\"}";
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
								if (null != writer) {
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
		Executable executable = new DownloadExecutable("download_file", "$params.path", true);
		CompletableFuture<Boolean> downloadFuture = scriptingApi.registerScript("download_file", executable, false, false)
				.thenComposeAsync(aBoolean -> {
					String scriptName = "download_file";
					return client.callScriptUrl(downloadUrl, JsonNode.class)
							.handle((jsonNode, ex) -> {
								String transactionId = jsonNode.get(scriptName).get("transaction_id").textValue();
								System.out.println("transactionId:" + transactionId);
								return (ex == null);
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
	public void testDownloadFile() {
		Executable executable = new DownloadExecutable("download_file", "$params.path", true);
		CompletableFuture<Boolean> downloadFuture = scriptingApi.registerScript("download_file", executable, false, false)
				.thenComposeAsync(aBoolean -> client.downloadFileByScriptUrl(downloadUrl, Reader.class).handle((reader, throwable) -> {
					if (throwable == null) {
						Utils.cacheTextFile(reader, testLocalCacheRootPath, "test.txt");
					} else {
						throwable.printStackTrace();
					}
					return throwable == null;
				}));

		try {
			assertTrue(downloadFuture.get());
			assertTrue(downloadFuture.isCompletedExceptionally() == false);
			assertTrue(downloadFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		try {
			assertTrue(downloadFuture.get());
			assertTrue(downloadFuture.isCompletedExceptionally() == false);
			assertTrue(downloadFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static Client client;
	private static Scripting scriptingApi;
	private static String textLocalPath;
	private static String testLocalCacheRootPath;

	@BeforeClass
	public static void setUp() {
		client = AppInstanceFactory.configSelector().getClient();
		scriptingApi = AppInstanceFactory.configSelector().getVault().getScripting();
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/";
		textLocalPath = localRootPath + "test.txt";
		testLocalCacheRootPath = localRootPath + "cache/script/";
	}

}
