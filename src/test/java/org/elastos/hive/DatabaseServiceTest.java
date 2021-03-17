package org.elastos.hive;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.database.Collation;
import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.CreateCollectionOptions;
import org.elastos.hive.database.Date;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.Index;
import org.elastos.hive.database.InsertManyResult;
import org.elastos.hive.database.InsertOneResult;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.MaxKey;
import org.elastos.hive.database.MinKey;
import org.elastos.hive.database.ObjectId;
import org.elastos.hive.database.ReadConcern;
import org.elastos.hive.database.ReadPreference;
import org.elastos.hive.database.RegularExpression;
import org.elastos.hive.database.Timestamp;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;
import org.elastos.hive.database.WriteConcern;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.DatabaseService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DatabaseServiceTest {

	private static DatabaseService databaseService;

	@Test
	@Order(1)
	public void testDbOptions() throws Exception {
		Collation collation = new Collation();
		collation.locale("en_us")
				.alternate(Collation.Alternate.SHIFTED)
				.backwards(true)
				.caseFirst(Collation.CaseFirst.OFF)
				.caseLevel(true)
				.maxVariable(Collation.MaxVariable.PUNCT)
				.normalization(true)
				.numericOrdering(false)
				.strength(Collation.Strength.PRIMARY);

		CountOptions co = new CountOptions();
		co.collation(collation)
				.hint(new Index("idx_01", Index.Order.ASCENDING))
				.limit(100)
				.maxTimeMS(1000)
				.skip(50);

		String json = co.serialize();
		co = CountOptions.deserialize(json);
		String json2 = co.serialize();
		assertEquals(json, json2);

		co = new CountOptions();
		co.hint(new Index[]{new Index("idx_01", Index.Order.ASCENDING),
				new Index("idx_02", Index.Order.DESCENDING)})
				.limit(100);

		json = co.serialize();
		co = CountOptions.deserialize(json);
		json2 = co.serialize();
		assertEquals(json, json2);

		collation = new Collation();
		collation.locale("en_us")
				.alternate(Collation.Alternate.SHIFTED)
				.normalization(true)
				.numericOrdering(false)
				.strength(Collation.Strength.PRIMARY);

		CreateCollectionOptions cco = new CreateCollectionOptions();
		cco.capped(true)
				.collation(collation)
				.max(10)
				.readConcern(ReadConcern.AVAILABLE)
				.readPreference(ReadPreference.PRIMARY_PREFERRED)
				.writeConcern(new WriteConcern(10, 100, true, false))
				.size(123456);

		json = cco.serialize();
		cco = CreateCollectionOptions.deserialize(json);
		json2 = cco.serialize();
		assertEquals(json, json2);

		WriteConcern wc = new WriteConcern();
		wc.fsync(true);
		wc.w(10);

		cco = new CreateCollectionOptions();
		cco.capped(true)
				.collation(collation)
				.readPreference(ReadPreference.PRIMARY_PREFERRED)
				.writeConcern(wc);

		json = cco.serialize();
		cco = CreateCollectionOptions.deserialize(json);
		json2 = cco.serialize();
		assertEquals(json, json2);

		DeleteOptions dopt = new DeleteOptions();
		dopt.collation(collation);

		json = dopt.serialize();
		dopt = DeleteOptions.deserialize(json);
		json2 = dopt.serialize();
		assertEquals(json, json2);

		FindOptions fo = new FindOptions();
		String projection = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

		fo.allowDiskUse(true)
				.batchSize(100)
				.collation(collation)
				.hint(new Index[]{new Index("didurl", Index.Order.ASCENDING), new Index("type", Index.Order.DESCENDING)})
				.projection(Utils.jsonToMap(projection))
				.max(10);

		json = fo.serialize();
		fo = FindOptions.deserialize(json);
		json2 = fo.serialize();
		assertEquals(json, json2);

		InsertOptions io = new InsertOptions();
		io.bypassDocumentValidation(true);

		json = io.serialize();
		io = InsertOptions.deserialize(json);
		json2 = io.serialize();
		assertEquals(json, json2);

		UpdateOptions uo = new UpdateOptions();
		uo.bypassDocumentValidation(true)
				.collation(collation)
				.upsert(true);

		json = uo.serialize();
		uo = UpdateOptions.deserialize(json);
		json2 = uo.serialize();
		assertEquals(json, json2);
	}

	@Test
	@Order(2)
	public void testDbResults() throws Exception {
		String json = "{\"deleted_count\":1000}";
		DeleteResult ds = DeleteResult.deserialize(json);
		assertEquals(1000, ds.deletedCount());
		json = ds.serialize();
		ds = DeleteResult.deserialize(json);
		assertEquals(1000, ds.deletedCount());

		json = "{\"acknowledged\":true,\"inserted_id\":\"test_inserted_id\"}";
		InsertOneResult ior = InsertOneResult.deserialize(json);
		assertTrue(ior.acknowledged());
		assertEquals("test_inserted_id", ior.insertedId());
		json = ior.serialize();
		ior = InsertOneResult.deserialize(json);
		assertTrue(ior.acknowledged());
		assertEquals("test_inserted_id", ior.insertedId());

		json = "{\"acknowledged\":false,\"inserted_ids\":[\"test_inserted_id1\",\"test_inserted_id2\"]}";
		InsertManyResult imr = InsertManyResult.deserialize(json);
		assertFalse(imr.acknowledged());
		List<String> ids = imr.insertedIds();
		assertNotNull(ids);
		assertEquals(2, ids.size());
		json = imr.serialize();
		imr = InsertManyResult.deserialize(json);
		assertFalse(imr.acknowledged());
		ids = imr.insertedIds();
		assertNotNull(ids);
		assertEquals(2, ids.size());

		json = "{\"matched_count\":10,\"modified_count\":5,\"upserted_count\":3,\"upserted_id\":\"test_id\"}";
		UpdateResult ur = UpdateResult.deserialize(json);
		assertEquals(10, ur.matchedCount());
		assertEquals(5, ur.modifiedCount());
		assertEquals(3, ur.upsertedCount());
		assertEquals("test_id", ur.upsertedId());
		json = ur.serialize();
		ur = UpdateResult.deserialize(json);
		assertEquals(10, ur.matchedCount());
		assertEquals(5, ur.modifiedCount());
		assertEquals(3, ur.upsertedCount());
		assertEquals("test_id", ur.upsertedId());
	}

	public static class TestDBDataTypes {
		@JsonProperty("testDate")
		protected Date date;
		@JsonProperty("testMaxKey")
		protected MaxKey maxKey;
		@JsonProperty("testMinKey")
		protected MinKey minKey;
		@JsonProperty("testObjectId")
		protected ObjectId oid;
		@JsonProperty("testTimestamp")
		protected Timestamp ts;
		@JsonProperty("testRegex")
		protected RegularExpression regex;

		protected TestDBDataTypes() {
		}
	}



	@BeforeClass
	public static void setUp() {
		try {
			databaseService = TestData.getInstance().newVault().getDatabaseService();
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
		}
	}
}
