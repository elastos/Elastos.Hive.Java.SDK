package org.elastos.hive.vault.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;

/**
 * The wrapper class is to access the database module of the hive node.
 */
public class DatabaseController {
	private DatabaseAPI databaseAPI;

	/**
	 * Create by the RPC connection.
	 *
	 * @param connection The RPC connection.
	 */
	public DatabaseController(NodeRPCConnection connection) {
		databaseAPI = connection.createService(DatabaseAPI.class, true);
	}

	/**
	 * Create the collection.
	 *
	 * @param collectionName The name of the collection.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void createCollection(String collectionName) throws HiveException {
		try {
			CreateCollectionResult result;

			result = databaseAPI.createCollection(collectionName).execute().body();
			if (!collectionName.equals(result.getName()))
				throw new ServerUnknownException("Different collection created, impossible to happen");

		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.ALREADY_EXISTS:
					throw new AlreadyExistsException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Delete the collection by name.
	 *
	 * @param collectionName The name of the collection.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void deleteCollection(String collectionName) throws HiveException {
		try {
			databaseAPI.deleteCollection(collectionName).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get user's collections.
	 *
	 * @return Collection list.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<Collection> getCollections() throws HiveException {
		try {
			return databaseAPI.getCollections().execute().body().getCollections();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Insert one document.
	 *
	 * @param collectionName The name of the collection.
	 * @param document The document.
	 * @param options Insert options.
	 * @return The details of the insert operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public InsertResult insertOne(String collectionName,
								  JsonNode document,
								  InsertOptions options) throws HiveException {
		return insertMany(collectionName, Collections.singletonList(document), options);
	}

	/**
	 * Insert many documents.
	 *
	 * @param collectionName The name of the collection.
	 * @param documents The document.
	 * @param options Insert options.
	 * @return The details of the insert operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public InsertResult insertMany(String collectionName,
								   List<JsonNode> documents,
								   InsertOptions options) throws HiveException {
		try {
			return databaseAPI.insert(collectionName, new InsertParams(documents, options)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Update the first matched document by the filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to filter the matched document.
	 * @param update The update data.
	 * @param options The update options.
	 * @return The details of the update operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public UpdateResult updateOne(String collectionName, JsonNode filter,
								  JsonNode update, UpdateOptions options) throws HiveException {
		return updateInternal(collectionName, false, filter, update, options);
	}

	/**
	 * Update all matched documents by the filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to filter the matched document.
	 * @param update The update data.
	 * @param options The update options.
	 * @return The details of the update operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public UpdateResult updateMany(String collectionName,
								   JsonNode filter,
								   JsonNode update,
								   UpdateOptions options) throws HiveException {
		return updateInternal(collectionName, true, filter, update, options);
	}

	private UpdateResult updateInternal(String collectionName,
										boolean updateOne,
										JsonNode filter,
										JsonNode update,
										UpdateOptions options) throws HiveException {
		try {
			return databaseAPI.update(collectionName,
					updateOne ? "true" : "false",
					new UpdateParams(filter, update, options)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Delete the first matched document by the filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to filter the matched document.
	 * @param options The delete options.
	 * @return The details of the delete operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public int deleteOne(String collectionName, JsonNode filter, DeleteOptions options) throws HiveException {
		return deleteInternal(collectionName, false, filter, options);
	}

	/**
	 * Delete all matched document by the filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to filter the matched documents.
	 * @param options The delete options.
	 * @return The details of the delete operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public int deleteMany(String collectionName, JsonNode filter, DeleteOptions options) throws HiveException {
		return deleteInternal(collectionName, true, filter, options);
	}

	private int deleteInternal(String collectionName, boolean deleteOne, JsonNode filter, DeleteOptions options)
			throws HiveException {
		try {
			// TODO: refine delete API to return something first.
			databaseAPI.delete(collectionName,
					deleteOne ? "true" : "false",
					new DeleteParams(filter, options)).execute().body();
			return 0;
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Count the documents by filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to match the documents.
	 * @param options Count options.
	 * @return The number of the matched documents.
	 * @throws HiveException The error comes from the hive node.
	 */
	public long countDocuments(String collectionName, JsonNode filter, CountOptions options) throws HiveException {
		try {
			return databaseAPI.count(collectionName, new CountParams(filter, options)).execute().body().getCount();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Find the first matched document by filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to match the document.
	 * @param options The find options.
	 * @return The first matched document for the find operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public JsonNode findOne(String collectionName, JsonNode filter, FindOptions options) throws HiveException {
		List<JsonNode> docs = find(collectionName, filter, options);
		return docs != null && !docs.isEmpty() ? docs.get(0) : null;
	}

	/**
	 * Find all matched document by filter.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to match the documents.
	 * @param options The find options.
	 * @return All matched documents for the find operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<JsonNode> find(String collectionName, JsonNode filter, FindOptions options) throws HiveException {
		try {
			String filterStr = filter == null ? "" : filter.toString();
			String skip = options != null ? options.getSkipStr() : "";
			String limit = options != null ? options.getLimitStr() : "";
			return databaseAPI.find(collectionName, filterStr, skip, limit).execute().body().getDocuments();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Query the documents by filter and options.
	 *
	 * @param collectionName The name of the collection.
	 * @param filter The filter to match the documents.
	 * @param options The query options.
	 * @return All matched documents for the query operation.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<JsonNode> query(String collectionName, JsonNode filter, QueryOptions options) throws HiveException {
		try {
			return databaseAPI.query(new QueryParams(collectionName, filter, options)).execute().body().getDocuments();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
