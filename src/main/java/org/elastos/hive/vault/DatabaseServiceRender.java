package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.Vault;
import org.elastos.hive.database.*;
import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import org.elastos.hive.service.DatabaseService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class DatabaseServiceRender extends BaseServiceRender implements DatabaseService, ExceptionConvertor {

	public DatabaseServiceRender(Vault vault) {
		super(vault);
	}

	@Override
	public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
								.createCollection(new CreateCollectionRequestBody(name))
								.execute()
								.body());
				return true;
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> deleteCollection(String name) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
								.deleteCollection(new DeleteCollectionRequestBody(name))
								.execute()
								.body());
				return true;
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOneOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				InsertDocResponseBody body = HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
						.insertOne(new InsertDocRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(doc),
								options))
						.execute()
						.body());
				return new InsertOneResult()
						.setInsertedId(body.getInsertedId())
						.setAcknowledged(body.getAcknowledged());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertManyOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				InsertDocsResponseBody body = HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
								.insertMany(new InsertDocsRequestBody(collection,
										HiveResponseBody.jsonNodeList2KeyValueDicList(docs),
										options))
								.execute()
								.body());
				return new InsertManyResult()
						.setInsertedIds(body.getInsertedIds())
						.setAcknowledged(body.getAcknowledged());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
								.countDocs(new CountDocRequestBody(
										collection,
										HiveResponseBody.jsonNode2KeyValueDic(query),
										options))
								.execute()
								.body()).getCount();
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.KeyValueDict2JsonNode(
						HiveResponseBody.validateBody(
								getConnectionManager().getDatabaseApi()
										.findOne(new FindDocRequestBody(collection,
												HiveResponseBody.jsonNode2KeyValueDic(query),
												options))
										.execute()
										.body()).getItem());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.KeyValueDictList2JsonNodeList(
						HiveResponseBody.validateBody(
								getConnectionManager().getDatabaseApi()
						.findMany(new FindDocsRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(query),
								options))
						.execute()
						.body()).getItems());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				UpdateDocResponseBody body = HiveResponseBody.validateBody(
								getConnectionManager().getDatabaseApi()
										.updateOne(new UpdateDocRequestBody(collection)
											.setFilter(HiveResponseBody.jsonNode2KeyValueDic(filter))
											.setUpdate(HiveResponseBody.jsonNode2KeyValueDic(update))
										.setOptions(options))
										.execute()
										.body());
				return new UpdateResult()
						.setMatchedCount(body.getMatchedCount())
						.setModifiedCount(body.getModifiedCount())
						.setAcknowledged(body.getAcknowledged())
						.setUpsertedId(body.getUpsertedId());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				UpdateDocResponseBody body = HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
								.updateMany(new UpdateDocRequestBody(collection)
										.setFilter(HiveResponseBody.jsonNode2KeyValueDic(filter))
										.setUpdate(HiveResponseBody.jsonNode2KeyValueDic(update))
										.setOptions(options))
								.execute()
								.body());
				return new UpdateResult()
						.setMatchedCount(body.getMatchedCount())
						.setModifiedCount(body.getModifiedCount())
						.setAcknowledged(body.getAcknowledged())
						.setUpsertedId(body.getUpsertedId());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				DeleteDocResponseBody body = HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
						.deleteOne(new DeleteDocRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(filter)))
						.execute()
						.body());
				return new DeleteResult()
						.setDeletedCount(body.getDeletedCount())
						.setAcknowledged(body.getAcknowledged());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}

	@Override
	public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				DeleteDocResponseBody body = HiveResponseBody.validateBody(
						getConnectionManager().getDatabaseApi()
								.deleteMany(new DeleteDocRequestBody(collection,
										HiveResponseBody.jsonNode2KeyValueDic(filter)))
								.execute()
								.body());
				return new DeleteResult()
						.setDeletedCount(body.getDeletedCount())
						.setAcknowledged(body.getAcknowledged());
			} catch (Exception e) {
				throw new CompletionException(toHiveException(e));
			}
		});
	}
}
