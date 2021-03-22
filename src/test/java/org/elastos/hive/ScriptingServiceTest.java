package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.*;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.ScriptingService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptingServiceTest {
	private static final String CONDITION_NAME = "get_group_messages";
	private static final String NO_CONDITION_NAME = "script_no_condition";
	private static final String UPLOAD_FILE_NAME = "upload_file";

	private static final String REMOTE_FILE = "test.txt";

	@Test
	public void test01_registerScriptFind() {
		try {
			ScriptKvItem filter = new ScriptKvItem().putKv("_id","$params.group_id")
					.putKv("friends", "$callScripter_did");
			Boolean isSuccess = scriptingService.registerScript(CONDITION_NAME,
					new Condition("verify_user_permission", "queryHasResults",
							new ScriptFindBody("test_group", filter)),
					new Executable(CONDITION_NAME, Executable.TYPE_FIND,
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
	public void test02_registerScriptFindWithoutCondition() {
		try {
			Boolean isSuccess = scriptingService.registerScript(NO_CONDITION_NAME,
					new Executable("get_groups", Executable.TYPE_FIND,
							new ScriptFindBody("groups",
									new ScriptKvItem().putKv("friends","$caller_did"))),
					false, false)
					.exceptionally(e->{
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
	public void test03_callScriptFindWithoutCondition() {
		//TODO: A bug on node did_scripting.py line 121: return col maybe None.
		try {
			String result = scriptingService.callScript(NO_CONDITION_NAME,
					null, "appId", String.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test03_callScriptUrlFindWithoutCondition() {
		//TODO: A bug on node did_scripting.py line 121: return col maybe None.
		try {
			String result = scriptingService.callScriptUrl(UPLOAD_FILE_NAME,
					null, "appId", String.class)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test03_uploadFile() {
		registerScriptFileUpload();
		callScriptFileUpload();
		uploadFileByTransActionId();
	}

	private void uploadFileByTransActionId() {
		try {
			Boolean isSuccess = scriptingService.registerScript(NO_CONDITION_NAME,
					new Executable("get_groups", Executable.TYPE_FIND,
							new ScriptFindBody("groups",
									new ScriptKvItem().putKv("friends","$caller_did"))),
					false, false)
					.exceptionally(e->{
						fail();
						return null;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void callScriptFileUpload() {

	}

	private void registerScriptFileUpload() {

	}

	@BeforeClass
	public static void setUp() {
		try {
			scriptingService = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getScriptingService()).join();
			filesApi = TestData.getInstance().getVault().thenApplyAsync(vault -> vault.getFilesService()).join();
		} catch (HiveException|DIDException e) {
			e.printStackTrace();
		}
	}

	private static ScriptingService scriptingService;
	private static FilesService filesApi;

	public ScriptingServiceTest() {
	}
}
