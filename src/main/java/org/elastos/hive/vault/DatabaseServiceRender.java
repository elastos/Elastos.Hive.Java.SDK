package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.Vault;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.CreateCollectionOptions;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.InsertManyResult;
import org.elastos.hive.database.InsertOneResult;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import org.elastos.hive.service.DatabaseService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class DatabaseServiceRender implements DatabaseService {

	private ConnectionManager connectionManager;

	public DatabaseServiceRender(Vault vault) {
		this.connectionManager = vault.getAppContext().getConnectionManager();
	}

	@Override
	public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
								.createCollection(new CreateCollectionRequestBody(name))
								.execute()
								.body());
				return true;
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> deleteCollection(String name) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
								.deleteCollection(new DeleteCollectionRequestBody(name))
								.execute()
								.body());
				return true;
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				InsertDocResponseBody body = HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
						.insertOne(new InsertDocRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(doc),
								options))
						.execute()
						.body());
				return new InsertOneResult()
						.setInsertedId(body.getInsertedId())
						.setAcknowledged(body.getAcknowledged());
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				InsertDocsResponseBody body = HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
								.insertMany(new InsertDocsRequestBody(collection,
										HiveResponseBody.jsonNodeList2KeyValueDicList(docs),
										options))
								.execute()
								.body());
				return new InsertManyResult()
						.setInsertedIds(body.getInsertedIds())
						.setAcknowledged(body.getAcknowledged());
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
								.countDocs(new CountDocRequestBody(
										collection,
										HiveResponseBody.jsonNode2KeyValueDic(query),
										options))
								.execute()
								.body()).getCount();
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.KeyValueDict2JsonNode(
						HiveResponseBody.validateBody(
								connectionManager.getDatabaseApi()
										.findOne(new FindDocRequestBody(collection,
												HiveResponseBody.jsonNode2KeyValueDic(query),
												options))
										.execute()
										.body()).getItem());
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HiveResponseBody.KeyValueDictList2JsonNodeList(
						HiveResponseBody.validateBody(
								connectionManager.getDatabaseApi()
						.findMany(new FindDocsRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(query),
								options))
						.execute()
						.body()).getItems());
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				UpdateDocResponseBody body = HiveResponseBody.validateBody(
								connectionManager.getDatabaseApi()
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
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				UpdateDocResponseBody body = HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
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
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				DeleteDocResponseBody body = HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
						.deleteOne(new DeleteDocRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(filter)))
						.execute()
						.body());
				return new DeleteResult()
						.setDeletedCount(body.getDeletedCount())
						.setAcknowledged(body.getAcknowledged());
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}

	@Override
	public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				DeleteDocResponseBody body = HiveResponseBody.validateBody(
						connectionManager.getDatabaseApi()
								.deleteMany(new DeleteDocRequestBody(collection,
										HiveResponseBody.jsonNode2KeyValueDic(filter)))
								.execute()
								.body());
				return new DeleteResult()
						.setDeletedCount(body.getDeletedCount())
						.setAcknowledged(body.getAcknowledged());
			} catch (IOException | HiveException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		});
	}
}
