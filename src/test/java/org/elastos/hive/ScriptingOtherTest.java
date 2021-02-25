package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.scripting.DbFindQuery;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.utils.JsonUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class ScriptingOtherTest {

	@Test
	public void test00_callOtherScript() {
		String scriptName = "get_groups";
		CompletableFuture<Boolean> future = scripting.thenComposeAsync(scripting -> {
							JsonNode filter = JsonUtil.deserialize("{\"friends\":\"$callScripter_did\"}");
							JsonNode options = JsonUtil.deserialize("{\"projection\":{\"_id\":false,\"name\":true}}");
							Executable executable = new DbFindQuery(scriptName, "groups", filter, options);
							return scripting.registerScript(scriptName, executable, false, false);
						})
				.thenComposeAsync(aBoolean ->
						crossScripting.thenComposeAsync(scripting ->
								scripting.callScript(scriptName, null, "appId", String.class)))
				.handle((s, throwable) -> (null==throwable));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static CompletableFuture<Scripting> scripting;
	private static CompletableFuture<Scripting> crossScripting;

	@BeforeClass
	public static void setUp() {
		try {
			scripting = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getScripting());
			crossScripting = TestData.getInstance().getCrossData().getCrossVault().thenApplyAsync(vault -> vault.getScripting());
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

}
