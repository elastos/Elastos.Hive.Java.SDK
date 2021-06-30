package org.elastos.hive;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.scripting.DbFindQuery;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.utils.JsonUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class ScriptingOtherTest {

	@Test @Disabled
	public void test00_callOtherScript() {
		String scriptName = "get_groups";
		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() ->
				AppInstanceFactory.initCrossConfig().getVault().getScripting())
				.thenComposeAsync(scripting ->
						{
							JsonNode filter = JsonUtil.deserialize("{\"friends\":\"$callScripter_did\"}");
							JsonNode options = JsonUtil.deserialize("{\"projection\":{\"_id\":false,\"name\":true}}");
							Executable executable = new DbFindQuery(scriptName, "groups", filter, options);
							return scripting.registerScript(scriptName, executable, false, false);
						})
				.thenApplyAsync(aBoolean ->
						aBoolean ? AppInstanceFactory.initCrossConfig().getVault().getScripting() : null)
				.thenComposeAsync(scripting ->
						scripting.callScript(scriptName, null, "appId", String.class))
				.handle((BiFunction<String, Throwable, Boolean>) (s, throwable) ->
						(throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
