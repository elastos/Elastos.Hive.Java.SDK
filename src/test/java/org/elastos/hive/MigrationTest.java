package org.elastos.hive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.backup.State;
import org.elastos.hive.config.TestData;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.exception.HiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class MigrationTest {

	private static final String collectionName = "migration";
	private static final String fileRemotePath = "migration/test.txt";

	@Order(1)
	@Test
	public void sourceVaultOperation() {
		Database database = sourceVault.getDatabase();
		CompletableFuture<Boolean> databaseFuture = database.createCollection(collectionName, null)
				.thenComposeAsync(aBoolean -> {
					ObjectNode docNode = JsonNodeFactory.instance.objectNode();
					docNode.put("author", "john doe1");
					docNode.put("title", "Eve for Dummies1");

					InsertOptions insertOptions = new InsertOptions();
					insertOptions.bypassDocumentValidation(false).ordered(true);
					return database.insertOne(collectionName, docNode, insertOptions);
				}).handleAsync((insertOneResult, throwable) -> (null == throwable));

		try {
			assertTrue(databaseFuture.get());
			assertTrue(databaseFuture.isCompletedExceptionally() == false);
			assertTrue(databaseFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Files files = sourceVault.getFiles();
		FileReader fileReader = null;
		Writer writer = null;
		try {
			writer = files.upload(fileRemotePath, Writer.class).exceptionally(e -> {
				System.out.println(e.getMessage());
				return null;
			}).get();
			writer.write("migration test txt");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (null != fileReader) fileReader.close();
				if (null != writer) writer.close();
			} catch (Exception e) {
				fail();
			}
		}
	}

	@Order(2)
	@Test
	public void testMigration() {
		CompletableFuture<Boolean> migrationFuture = managementApi.freezeVault()
				.thenComposeAsync(aBoolean ->
						backupApi.store(testData.getBackupAuthenticationHandler()))
				.thenApplyAsync(aBoolean -> {
					for (; ; ) {
						try {
							Thread.sleep(20 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						State state = backupApi.getState().join();
						if (state == State.SUCCESS) {
							return true;
						} else if(state == State.FAILED) {
							fail();
						}
					}
				}).thenComposeAsync(aBoolean ->
						targetBackupApi.activate()
				);

		try {
			assertTrue(migrationFuture.get());
			assertTrue(migrationFuture.isCompletedExceptionally() == false);
			assertTrue(migrationFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Order(3)
	@Test
	public void targetVaultOperation() {
		Database database = targetVault.getDatabase();
		ObjectNode query = JsonNodeFactory.instance.objectNode();
		query.put("author", "john doe1");

		FindOptions findOptions = new FindOptions();
		findOptions.skip(0)
				.allowPartialResults(false)
				.returnKey(false)
				.batchSize(0)
				.projection(jsonToMap("{\"_id\": false}"));

		CompletableFuture<Boolean> future = database.findOne(collectionName, query, findOptions)
				.handle((success, ex) -> (ex == null));
		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Files files = targetVault.getFiles();
		try {
			Reader reader = files.download(fileRemotePath, Reader.class).get();
			String txt = new BufferedReader(reader).readLine();
			System.out.println("txt:"+txt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	private static TestData testData;

	private static Backup backupApi;
	private static Backup targetBackupApi;
	private static Vault sourceVault;
	private static Vault targetVault;
	private static Management managementApi;

	@BeforeClass
	public static void setUp() {
		try {
			testData = TestData.getInstance();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
		managementApi = testData.getManagement().join();
		backupApi = testData.getBackup().join();
		sourceVault = testData.getVault().join();
		targetVault = testData.getTargetVault().join();
		targetBackupApi = testData.getTargetBackup().join();
	}

	@AfterClass
	public static void tearDown() {
		CompletableFuture<Boolean> future = managementApi.unfreezeVault();
		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static Map<String, Object> jsonToMap(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> p = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

