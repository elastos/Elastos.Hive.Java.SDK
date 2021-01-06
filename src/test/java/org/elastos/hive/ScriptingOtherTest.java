package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.scripting.DbFindQuery;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.utils.JsonUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScriptingOtherTest {

	@Test
	public void test00_callOtherScript() {
		String scriptName = "get_groups";
		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() ->
				AppInstanceFactory.getUser2().getVault().getScripting())
				.thenComposeAsync(scripting ->
						{
							JsonNode filter = JsonUtil.deserialize("{\"friends\":\"$callScripter_did\"}");
							JsonNode options = JsonUtil.deserialize("{\"projection\":{\"_id\":false,\"name\":true}}");
							Executable executable = new DbFindQuery(scriptName, "groups", filter, options);
							return scripting.registerScript(scriptName, executable, false, false);
						})
				.thenApplyAsync(aBoolean ->
						aBoolean ? AppInstanceFactory.getUser3().getVault().getScripting() : null)
				.thenComposeAsync(scripting ->
						scripting.callScript(scriptName, null, null, String.class))
				.handle((BiFunction<String, Throwable, Boolean>) (s, throwable) ->
						(throwable == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}
}
