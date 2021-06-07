package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.Vault;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.vault.database.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class DatabaseServiceRender implements DatabaseService {
	DatabaseController controller;

	public DatabaseServiceRender(Vault vault) {
		controller = new DatabaseController(vault);
	}

	@Override
	public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.createCollection(name);
				return true;
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> deleteCollection(String name) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				controller.deleteCollection(name);
				return true;
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOneOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.insertOne(collection, doc, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertManyOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.insertMany(collection, docs, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.countDocuments(collection, query, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.findOne(collection, query, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.findMany(collection, query, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.updateOne(collection, filter, update, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.updateMany(collection, filter, update, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.deleteOne(collection, filter, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.deleteMany(collection, filter, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}
}
