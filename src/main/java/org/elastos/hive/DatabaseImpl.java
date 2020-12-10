package org.elastos.hive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

class DatabaseImpl implements Database {
	private AuthHelper authHelper;
	private ConnectionManager connectionManager;

	DatabaseImpl(AuthHelper authHelper) {
		this.authHelper = authHelper;
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return createColImp(name);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean createColImp(String collection) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("collection", collection);
			String json = JsonUtil.serialize(map);

			Response response = this.connectionManager.getDatabaseApi()
					.createCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> deleteCollection(String name) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return deleteColImp(name);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private boolean deleteColImp(String collection) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("collection", collection);
			String json = JsonUtil.serialize(map);

			Response response = this.connectionManager.getDatabaseApi()
					.deleteCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return insertOneImp(collection, doc, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private InsertOneResult insertOneImp(String collection, JsonNode doc, InsertOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			rootNode.set("document", doc);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.insertOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			InsertOneResult insertResult = InsertOneResult.deserialize(ResponseHelper.toString(response));
			return insertResult;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return insertManyImp(collection, docs, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private InsertManyResult insertManyImp(String collection, List<JsonNode> docs, InsertOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
			arrayNode.addAll(docs);
			rootNode.put("collection", collection);
			rootNode.set("document", arrayNode);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.insertMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			InsertManyResult insertResult = InsertManyResult.deserialize(ResponseHelper.toString(response));
			return insertResult;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return countDocumentsImp(collection, query, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private long countDocumentsImp(String collection, JsonNode query, CountOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=query) rootNode.set("filter", query);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.countDocs(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);

			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			return ret.get("count").asLong();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return findOneImp(collection, query, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private JsonNode findOneImp(String collection, JsonNode query, FindOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=query) rootNode.set("filter", query);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.findOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			JsonNode jsonNode = ResponseHelper.getValue(response, JsonNode.class);
			JsonNode item = jsonNode.get("items");
			return item;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return findManyImp(collection, query, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private List<JsonNode> findManyImp(String collection, JsonNode query, FindOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=query) rootNode.set("filter", query);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.findMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			List<JsonNode> jsonNodes = ResponseHelper.getArray(response, "items");
			return jsonNodes;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return updateOneImp(collection, filter, update, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private UpdateResult updateOneImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=filter) rootNode.set("filter", filter);
			if(null!=update) rootNode.set("update", update);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.updateOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			String ret = ResponseHelper.toString(response);
			if(ret.contains("_error")) {
				HiveException exception = new HiveException(ret);
				throw exception;
			}
			UpdateResult updateResult = UpdateResult.deserialize(ret);
			return updateResult;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return updateManyImp(collection, filter, update, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private UpdateResult updateManyImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=filter) rootNode.set("filter", filter);
			if(null!=update) rootNode.set("update", update);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.updateMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			UpdateResult updateResult = UpdateResult.deserialize(ResponseHelper.toString(response));
			return updateResult;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return deleteOneImp(collection, filter, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private DeleteResult deleteOneImp(String collection, JsonNode filter, DeleteOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=filter) rootNode.set("filter", filter);

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.deleteOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			DeleteResult deleteResult = DeleteResult.deserialize(ResponseHelper.toString(response));
			return deleteResult;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return deleteManyImp(collection, filter, options);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private DeleteResult deleteManyImp(String collection, JsonNode filter, DeleteOptions options) throws HiveException {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=filter) rootNode.set("filter", filter);

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.deleteMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			authHelper.checkResponseWithRetry(response);
			DeleteResult deleteResult = DeleteResult.deserialize(ResponseHelper.toString(response));
			return deleteResult;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}
}
