package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

class DatabaseServiceRender implements DatabaseService {

	private ConnectionManager connectionManager;

	public DatabaseServiceRender(Vault vault) {
		this.connectionManager = vault.getAppContext().getConnectionManager();
	}

	@Override
	public CompletableFuture<Boolean> createCollection(String name, CreateCollectionOptions options) {
		return CompletableFuture.supplyAsync(() -> createColImp(name));
	}

	private boolean createColImp(String collection) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("collection", collection);
			String json = JsonUtil.serialize(map);

			Response response = this.connectionManager.getDatabaseApi()
					.createCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();
			return true;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<Boolean> deleteCollection(String name) {
		return CompletableFuture.supplyAsync(() -> deleteColImp(name));
	}

	private boolean deleteColImp(String collection) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("collection", collection);
			String json = JsonUtil.serialize(map);

			Response response = this.connectionManager.getDatabaseApi()
					.deleteCollection(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();
			return true;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<InsertOneResult> insertOne(String collection, JsonNode doc, InsertOptions options) {
		return CompletableFuture.supplyAsync(() -> insertOneImp(collection, doc, options));
	}

	private InsertOneResult insertOneImp(String collection, JsonNode doc, InsertOptions options) {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			rootNode.set("document", doc);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.insertOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			InsertOneResult insertResult = InsertOneResult.deserialize(ResponseHelper.toString(response));
			return insertResult;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<InsertManyResult> insertMany(String collection, List<JsonNode> docs, InsertOptions options) {
		return CompletableFuture.supplyAsync(() -> insertManyImp(collection, docs, options));
	}

	private InsertManyResult insertManyImp(String collection, List<JsonNode> docs, InsertOptions options) {
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

			InsertManyResult insertResult = InsertManyResult.deserialize(ResponseHelper.toString(response));
			return insertResult;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<Long> countDocuments(String collection, JsonNode query, CountOptions options) {
		return CompletableFuture.supplyAsync(() -> countDocumentsImp(collection, query, options));
	}

	private long countDocumentsImp(String collection, JsonNode query, CountOptions options) {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=query) rootNode.set("filter", query);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.countDocs(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			return ret.get("count").asLong();
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<JsonNode> findOne(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> findOneImp(collection, query, options));
	}

	private JsonNode findOneImp(String collection, JsonNode query, FindOptions options) {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=query) rootNode.set("filter", query);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.findOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			JsonNode jsonNode = ResponseHelper.getValue(response, JsonNode.class);
			JsonNode item = jsonNode.get("items");
			return item;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<List<JsonNode>> findMany(String collection, JsonNode query, FindOptions options) {
		return CompletableFuture.supplyAsync(() -> findManyImp(collection, query, options));
	}

	private List<JsonNode> findManyImp(String collection, JsonNode query, FindOptions options) {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=query) rootNode.set("filter", query);
			if(null!=options) rootNode.set("options", JsonUtil.deserialize(options.serialize()));

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.findMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			List<JsonNode> jsonNodes = ResponseHelper.getArray(response, "items");
			return jsonNodes;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<UpdateResult> updateOne(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> updateOneImp(collection, filter, update, options));
	}

	private UpdateResult updateOneImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
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

			String ret = ResponseHelper.toString(response);
			if(ret.contains("_error")) {
				HiveException exception = new HiveException(ret);
				throw exception;
			}
			UpdateResult updateResult = UpdateResult.deserialize(ret);
			return updateResult;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<UpdateResult> updateMany(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
		return CompletableFuture.supplyAsync(() -> updateManyImp(collection, filter, update, options));
	}

	private UpdateResult updateManyImp(String collection, JsonNode filter, JsonNode update, UpdateOptions options) {
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

			UpdateResult updateResult = UpdateResult.deserialize(ResponseHelper.toString(response));
			return updateResult;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<DeleteResult> deleteOne(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> deleteOneImp(collection, filter, options));
	}

	private DeleteResult deleteOneImp(String collection, JsonNode filter, DeleteOptions options) {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=filter) rootNode.set("filter", filter);

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.deleteOne(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			DeleteResult deleteResult = DeleteResult.deserialize(ResponseHelper.toString(response));
			return deleteResult;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}

	@Override
	public CompletableFuture<DeleteResult> deleteMany(String collection, JsonNode filter, DeleteOptions options) {
		return CompletableFuture.supplyAsync(() -> deleteManyImp(collection, filter, options));
	}

	private DeleteResult deleteManyImp(String collection, JsonNode filter, DeleteOptions options) {
		try {
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
			rootNode.put("collection", collection);
			if(null!=filter) rootNode.set("filter", filter);

			String json = rootNode.toString();
			Response response = this.connectionManager.getDatabaseApi()
					.deleteMany(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
					.execute();

			DeleteResult deleteResult = DeleteResult.deserialize(ResponseHelper.toString(response));
			return deleteResult;
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}
}
