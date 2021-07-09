package org.elastos.hive;

import java.io.Reader;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.scripting.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: to test this, please update this line in class Client:
 * 		return getVaultProvider(targetDid, null)
 * 	to:
 * 		return getVaultProvider(targetDid, "https://hive-testnet1.trinity-tech.io")
 */
@Disabled
public class HiveURLTest {

	private static final String SCRIPT_URL = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/get_file_info?" +
			"params={\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"" + ScriptingTest.REMOTE_UPLOAD_FILE + "\"}";
	private static final String SCRIPT_URL_DOWNLOAD = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/download_file?" +
			"params={\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"" + ScriptingTest.REMOTE_UPLOAD_FILE + "\"}";

	@Test @Disabled
	public void testGetHiveURL() {
		CompletableFuture<Boolean> future = client.parseHiveURL(SCRIPT_URL)
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
	public void testCallScriptUrl() {
		String scriptName = "get_file_info";
		HashExecutable hashExecutable = new HashExecutable("file_hash", "$params.path", true);
		PropertiesExecutable propertiesExecutable = new PropertiesExecutable("file_properties", "$params.path", true);
		AggregatedExecutable executable = new AggregatedExecutable("file_properties_and_hash",
				new Executable[]{hashExecutable, propertiesExecutable});
		ScriptingTest.registerScript(scripting, scriptName, executable);

		assertDoesNotThrow(() -> client.callScriptUrl(SCRIPT_URL, JsonNode.class)
				.whenComplete((jsonNode, throwable) -> {
			assertNull(throwable);
			assertTrue(jsonNode.has("file_hash"));
			assertTrue(jsonNode.has("file_properties"));
		}).get());
	}

	@Test
	public void testCallScriptUrl4Download() {
		String scriptName = "download_file";
		ScriptingTest.registerScript(scripting, scriptName,
				new DownloadExecutable("download_file", "$params.path", true));

		assertDoesNotThrow(() -> client.downloadFileByScriptUrl(SCRIPT_URL_DOWNLOAD, Reader.class)
				.whenComplete((reader, throwable) -> {
			assertNull(throwable);
			Utils.cacheTextFile(reader, ScriptingTest.getLocalScriptCacheDir(), "test.txt");
		}).get());
	}

	private static Client client;
	private static Scripting scripting;

	@BeforeEach
	public void setUp() {
		client = AppInstanceFactory.configSelector().getClient();
		scripting = AppInstanceFactory.configSelector().getVault().getScripting();
	}

}
