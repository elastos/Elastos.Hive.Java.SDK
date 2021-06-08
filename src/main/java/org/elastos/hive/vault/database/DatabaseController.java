package org.elastos.hive.vault.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.connection.KeyValueDict;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseController {
	private DatabaseAPI databaseAPI;

	public DatabaseController(ServiceEndpoint serviceEndpoint) {
		databaseAPI = serviceEndpoint.getConnectionManager().createService(DatabaseAPI.class, true);
	}

	public void createCollection(String collectionName) throws HiveException {
		try {
			databaseAPI.createCollection(collectionName).execute();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public void deleteCollection(String collectionName) throws HiveException {
		try {
			databaseAPI.deleteCollection(collectionName).execute();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public static KeyValueDict jsonNode2KeyValueDic(JsonNode node) {
		return new ObjectMapper().convertValue(node, new TypeReference<KeyValueDict>() {});
	}

	public static List<KeyValueDict> jsonNodeList2KeyValueDicList(List<JsonNode> docs) {
		return docs.stream().map(DatabaseController::jsonNode2KeyValueDic).collect(Collectors.toList());
	}

	public static String jsonNode2Str(JsonNode node) {
		if (node == null)
			return "";
		try {
			return new ObjectMapper().writeValueAsString(node);
		} catch (JsonProcessingException e) {
			throw new InvalidParameterException("Invalid parameter of json node.");
		}
	}

	public InsertResult insertOne(String collectionName,
								  JsonNode doc,
								  InsertOptions options) throws HiveException {
		return insertMany(collectionName, Collections.singletonList(doc), options);
	}

	public InsertResult insertMany(String collectionName,
								   List<JsonNode> docs,
								   InsertOptions options) throws HiveException {
		try {
			return databaseAPI.insert(collectionName, new InsertRequest()
					.setDocuments(jsonNodeList2KeyValueDicList(docs))
					.setOptions(options)
			).execute().body();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public UpdateResult updateMany(String collectionName, JsonNode filter,
								   JsonNode update,
								   UpdateOptions options) throws HiveException {
		try {
			return databaseAPI.update(collectionName, new UpdateRequest()
					.setFilter(jsonNode2KeyValueDic(filter))
					.setUpdate(jsonNode2KeyValueDic(update))
					.setOptions(options)
			).execute().body();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public void deleteMany(String collectionName, JsonNode filter) throws HiveException {
		try {
			databaseAPI.delete(collectionName, new DeleteRequest(jsonNode2KeyValueDic(filter))).execute();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public Long countDocuments(String collectionName, JsonNode filter, CountOptions options) throws HiveException {
		try {
			return databaseAPI.count(collectionName, new CountRequest(jsonNode2KeyValueDic(filter), options))
					.execute().body().getCount();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public List<JsonNode> find(String collectionName, JsonNode filter, FindOptions options) throws HiveException {
		try {
			String skip = options != null ? options.getSkipStr() : "";
			String limit = options != null ? options.getLimitStr() : "";
			return HiveResponseBody.KeyValueDictList2JsonNodeList(
					databaseAPI.find(collectionName, jsonNode2Str(filter), skip, limit)
							.execute().body().getItems());
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public List<JsonNode> query(String collectionName, JsonNode filter, QueryOptions options) throws HiveException {
		try {
			return HiveResponseBody.KeyValueDictList2JsonNodeList(
					databaseAPI.query(new QueryRequest()
							.setCollectionName(collectionName)
							.setFilter(jsonNode2KeyValueDic(filter))
							.setOptions(options)
					).execute().body().getItems());
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}
}
