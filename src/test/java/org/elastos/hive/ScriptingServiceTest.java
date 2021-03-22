package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.*;
import org.elastos.hive.service.ScriptingService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptingServiceTest {

	@Test
	public void test01_registerScriptFind() {
		try {
			ScriptKvItem filter = new ScriptKvItem().putKv("_id","$params.group_id")
					.putKv("friends", "$callScripter_did");
			Boolean isSuccess = scriptingService.registerScript("get_group_messages",
					new Condition("verify_user_permission", "queryHasResults",
							new ScriptFindBody("test_group", filter)),
					new Executable("get_groups", Executable.TYPE_FIND,
							new ScriptFindBody("test_group", filter)),
					false, false).exceptionally(e->{
						fail();
						return null;
				}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test02_callScriptFind() {

	}

	@BeforeClass
	public static void setUp() {
		try {
			scriptingService = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getScriptingService()).join();
		} catch (HiveException|DIDException e) {
			e.printStackTrace();
		}
	}

	private static ScriptingService scriptingService;

	public ScriptingServiceTest() {
	}
}
