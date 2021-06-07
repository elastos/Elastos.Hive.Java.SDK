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
	public CompletableFuture<Boolean> createCollection(String name) {
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
	public CompletableFuture<InsertDocumentsResponse> insertOne(String collection, JsonNode doc, InsertDocumentsOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.insertOne(collection, doc, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<InsertDocumentsResponse> insertMany(String collection, List<JsonNode> docs, InsertDocumentsOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.insertMany(collection, docs, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountDocumentOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.countDocuments(collection, query, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<JsonNode>> findOne(String collection, JsonNode query, FindOptions options) {
		return findMany(collection, query, options);
	}

	@Override
	public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.find(collection, query, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<List<JsonNode>> query(String collection, JsonNode query, QueryDocumentsOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.query(collection, query, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<UpdateDocumentsResponse> updateOne(String collection, JsonNode filter, JsonNode update, UpdateDocumentsOptions options) {
		return updateMany(collection, filter, update, options);
	}

	@Override
	public CompletableFuture<UpdateDocumentsResponse> updateMany(String collection, JsonNode filter, JsonNode update, UpdateDocumentsOptions options) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return controller.updateMany(collection, filter, update, options);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}

	@Override
	public CompletableFuture<Void> deleteOne(String collection, JsonNode filter) {
		return deleteMany(collection, filter);
	}

	@Override
	public CompletableFuture<Void> deleteMany(String collection, JsonNode filter) {
		return CompletableFuture.runAsync(() -> {
			try {
				controller.deleteMany(collection, filter);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}
}
