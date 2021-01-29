package org.elastos.hive;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.scripting.AggregatedExecutable;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.HashExecutable;
import org.elastos.hive.scripting.PropertiesExecutable;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class HiveURLTest {

	private final String scriptUrl = "hive://did:elastos:icXtpDnZRSDrjmD5NQt6TYSphFRqoo2q6n@appId/get_file_info?params={\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";

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

	@BeforeClass
	public static void setUp() {
		client = AppInstanceFactory.configSelector().getClient();
		scriptingApi = AppInstanceFactory.configSelector().getVault().getScripting();
	}

}
