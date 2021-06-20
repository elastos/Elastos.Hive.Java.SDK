package org.elastos.hive.vault.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseController {
	private DatabaseAPI databaseAPI;

	public DatabaseController(NodeRPCConnection connection) {
		databaseAPI = connection.createService(DatabaseAPI.class, true);
	}

	public void createCollection(String collectionName) throws HiveException {
		try {
			CreateCollectionResult result;

			result = databaseAPI.createCollection(collectionName).execute().body();
			if (!collectionName.equals(result.getName()))
				throw new ServerUnkownException("Different collection created, impossible to happen");

		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);
			// TODO:
			// case1: collection already exist.
			default:
				throw new ServerUnkownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public void deleteCollection(String collectionName) throws HiveException {
		try {
			databaseAPI.deleteCollection(collectionName).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
			case NodeRPCException.UNAUTHORIZED:
				throw new UnauthorizedException(e);
			default:
				throw new ServerUnkownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
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
								  JsonNode document,
								  InsertOptions options) throws HiveException {
		return insertMany(collectionName, Collections.singletonList(document), options);
	}

	public InsertResult insertMany(String collectionName,
								   List<JsonNode> documents,
								   InsertOptions options) throws HiveException {
		try {
			return databaseAPI.insert(collectionName, new InsertParams(documents, options)).execute().body();
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnkownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public UpdateResult updateOne(String collectionName, JsonNode filter,
								  JsonNode update, UpdateOptions options) throws HiveException {
		throw new NotImplementedException();
	}

	public UpdateResult updateMany(String collectionName, JsonNode filter,
								   JsonNode update,
								   UpdateOptions options) throws HiveException {
		try {
			return databaseAPI.update(collectionName, new UpdateParams(filter, update, options)).execute().body();
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnkownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public int deleteOne(String collection, JsonNode filter, DeleteOptions options) throws HiveException {
		throw new NotImplementedException();
	}

	public int deleteMany(String collectionName, JsonNode filter, DeleteOptions options) throws HiveException {
		try {
			// TODO:
			databaseAPI.delete(collectionName, new DeleteParams(filter, options)).execute().body();
			return 0;
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnkownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public long countDocuments(String collectionName, JsonNode filter, CountOptions options) throws HiveException {
		try {
			return databaseAPI.count(collectionName, new CountParams(filter, options))
					.execute().body().getCount();
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnkownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public JsonNode findOne(String collectionName, JsonNode filter, FindOptions options) throws HiveException {
		List<JsonNode> docs = find(collectionName, filter, options);
		return docs != null && !docs.isEmpty() ? docs.get(0) : null;
	}

	public List<JsonNode> find(String collectionName, JsonNode filter, FindOptions options) throws HiveException {
		try {
			String skip = options != null ? options.getSkipStr() : "";
			String limit = options != null ? options.getLimitStr() : "";
			return databaseAPI.find(collectionName, jsonNode2Str(filter), skip, limit)
						.execute().body().documents();
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnkownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public List<JsonNode> query(String collectionName, JsonNode filter, QueryOptions options) throws HiveException {
		try {
			return databaseAPI.query(new QueryParams(collectionName, filter, options)).execute().body().documents();
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnkownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
