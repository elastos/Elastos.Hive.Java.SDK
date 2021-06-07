package org.elastos.hive.vault.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.connection.KeyValueDict;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;

import java.io.IOException;
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

	public InsertDocumentsResponse insertOne(String collectionName,
									 JsonNode doc,
									 InsertDocumentsOptions options) throws HiveException {
		return insertMany(collectionName, Collections.singletonList(doc), options);
	}

	public InsertDocumentsResponse insertMany(String collectionName,
									   List<JsonNode> docs,
									   InsertDocumentsOptions options) throws HiveException {
		try {
			return databaseAPI.insertDocuments(collectionName, new InsertDocumentsRequest()
					.setDocuments(jsonNodeList2KeyValueDicList(docs))
					.setOptions(options)
			).execute().body();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public UpdateDocumentsResponse updateMany(String collectionName, JsonNode filter,
								   JsonNode update,
								   UpdateDocumentsOptions options) throws HiveException {
		try {
			return databaseAPI.updateDocuments(collectionName, new UpdateDocumentsRequest()
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
			databaseAPI.deleteDocuments(collectionName, new DeleteDocumentsRequest(jsonNode2KeyValueDic(filter))).execute();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public Long countDocuments(String collectionName, JsonNode filter, CountDocumentOptions options) throws HiveException {
		try {
			return databaseAPI.countDocuments(collectionName, new CountDocumentRequest(jsonNode2KeyValueDic(filter), options))
					.execute().body().getCount();
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public List<JsonNode> find(String collectionName, JsonNode filter, FindOptions options) throws HiveException {
		try {
			return HiveResponseBody.KeyValueDictList2JsonNodeList(
					databaseAPI.findDocuments(collectionName,
							jsonNode2KeyValueDic(filter), options.getSkip(), options.getLimit())
					.execute().body().getItems());
		} catch (IOException e) {
			// TODO:
			throw new NetworkException(e.getMessage());
		}
	}

	public List<JsonNode> query(String collectionName, JsonNode filter, QueryDocumentsOptions options) throws HiveException {
		try {
			return HiveResponseBody.KeyValueDictList2JsonNodeList(
					databaseAPI.queryDocuments(new QueryDocumentsRequest()
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
