package org.elastos.hive.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.hive.Client;
import org.elastos.hive.Scripting;
import org.elastos.hive.Utils;
import org.elastos.hive.scripting.DownloadExecutable;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.UploadExecutable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HiveUrlController extends Controller {

	private static HiveUrlController mInstance = null;
	private Client client;
	private Scripting scripting;

	public static HiveUrlController newInstance(Client client, Scripting scripting) {
		if(mInstance == null) {
			mInstance = new HiveUrlController(client, scripting);
		}

		return mInstance;
	}

	private HiveUrlController(Client client, Scripting scripting) {
		this.client = client;
		this.scripting = scripting;
	}

	@Override
	void execute() {
		getHiveURL();
		uploadFile();
		downloadFile();
	}

	public void getHiveURL() {
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

	public void uploadFile() {
		Executable executable = new UploadExecutable("upload_file", "$params.path", true);
		CompletableFuture<Boolean> future = scripting.registerScript("upload_file", executable, false, false)
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

					return scripting.callScript(scriptName, params, "appId", JsonNode.class)
							.thenComposeAsync(jsonNode -> {
								String transactionId = jsonNode.get(scriptName).get("transaction_id").textValue();
								return scripting.uploadFile(transactionId, Writer.class);
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

	public void testCallScriptUrl() {
		Executable executable = new DownloadExecutable("download_file", "$params.path", true);
		CompletableFuture<Boolean> downloadFuture = scripting.registerScript("download_file", executable, false, false)
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

	
	public void downloadFile() {
		Executable executable = new DownloadExecutable("download_file", "$params.path", true);
		CompletableFuture<Boolean> downloadFuture = scripting.registerScript("download_file", executable, false, false)
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

	private final String downloadUrl = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/scripting/download_file?params={\"path\":\"test.txt\"}";
	private String textLocalPath;
	private String testLocalCacheRootPath;
	@Override
	protected void setUp() {
		super.setUp();
		String localRootPath = System.getProperty("user.dir") + "/src/test/resources/";
		textLocalPath = localRootPath + "local/test.txt";
		testLocalCacheRootPath = localRootPath + "cache/script/";
	}
}
