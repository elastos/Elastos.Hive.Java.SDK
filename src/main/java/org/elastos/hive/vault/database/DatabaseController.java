package org.elastos.hive.vault.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;

import java.io.IOException;
import java.util.List;

public class DatabaseController {
	private DatabaseAPI databaseAPI;

	public DatabaseController(ServiceEndpoint serviceEndpoint) {
		databaseAPI = serviceEndpoint.getConnectionManager().createService(DatabaseAPI.class, true);
	}

	public void createCollection(String collectionName) throws IOException {
		HiveResponseBody.validateBody(databaseAPI.createCollection(new CreateCollectionRequestBody(collectionName)).execute().body());
	}

	public void deleteCollection(String collectionName) throws IOException {
		HiveResponseBody.validateBody(databaseAPI.deleteCollection(new DeleteCollectionRequestBody(collectionName)).execute().body());
	}

	public InsertOneResult insertOne(String collectionName, JsonNode doc, InsertOneOptions options) throws IOException {
		InsertDocResponseBody body = HiveResponseBody.validateBody(
				databaseAPI.insertOne(new InsertDocRequestBody(collectionName,
								HiveResponseBody.jsonNode2KeyValueDic(doc),
								options)).execute().body());
		return new InsertOneResult()
				.setInsertedId(body.getInsertedId())
				.setAcknowledged(body.getAcknowledged());
	}

	public InsertManyResult insertMany(String collectionName, List<JsonNode> docs, InsertManyOptions options) throws IOException {
		InsertDocsResponseBody body = HiveResponseBody.validateBody(
				databaseAPI.insertMany(new InsertDocsRequestBody(collectionName,
								HiveResponseBody.jsonNodeList2KeyValueDicList(docs),
								options)).execute().body());
		return new InsertManyResult()
				.setInsertedIds(body.getInsertedIds())
				.setAcknowledged(body.getAcknowledged());
	}

	public UpdateResult updateOne(String collectionName, JsonNode filter, JsonNode update, UpdateOptions options) throws IOException {
		UpdateDocResponseBody body = HiveResponseBody.validateBody(
				databaseAPI.updateOne(new UpdateDocRequestBody(collectionName)
								.setFilter(HiveResponseBody.jsonNode2KeyValueDic(filter))
								.setUpdate(HiveResponseBody.jsonNode2KeyValueDic(update))
								.setOptions(options)).execute().body());
		return new UpdateResult()
				.setMatchedCount(body.getMatchedCount())
				.setModifiedCount(body.getModifiedCount())
				.setAcknowledged(body.getAcknowledged())
				.setUpsertedId(body.getUpsertedId());
	}

	public UpdateResult updateMany(String collectionName, JsonNode filter, JsonNode update, UpdateOptions options) throws IOException {
		UpdateDocResponseBody body = HiveResponseBody.validateBody(
				databaseAPI.updateMany(new UpdateDocRequestBody(collectionName)
						.setFilter(HiveResponseBody.jsonNode2KeyValueDic(filter))
						.setUpdate(HiveResponseBody.jsonNode2KeyValueDic(update))
						.setOptions(options)).execute().body());
		return new UpdateResult()
				.setMatchedCount(body.getMatchedCount())
				.setModifiedCount(body.getModifiedCount())
				.setAcknowledged(body.getAcknowledged())
				.setUpsertedId(body.getUpsertedId());
	}

	public DeleteResult deleteOne(String collectionName, JsonNode filter, DeleteOptions options) throws IOException {
		DeleteDocResponseBody body = HiveResponseBody.validateBody(
				databaseAPI.deleteOne(new DeleteDocRequestBody(collectionName,
						HiveResponseBody.jsonNode2KeyValueDic(filter))).execute().body());
		return new DeleteResult()
				.setDeletedCount(body.getDeletedCount())
				.setAcknowledged(body.getAcknowledged());
	}

	public DeleteResult deleteMany(String collectionName, JsonNode filter, DeleteOptions options) throws IOException {
		DeleteDocResponseBody body = HiveResponseBody.validateBody(
				databaseAPI.deleteMany(new DeleteDocRequestBody(collectionName,
						HiveResponseBody.jsonNode2KeyValueDic(filter))).execute().body());
		return new DeleteResult()
				.setDeletedCount(body.getDeletedCount())
				.setAcknowledged(body.getAcknowledged());
	}

	public Long countDocuments(String collectionName, JsonNode query, CountOptions options) throws IOException {
		return HiveResponseBody.validateBody(
				databaseAPI.countDocs(new CountDocRequestBody(
								collectionName,
								HiveResponseBody.jsonNode2KeyValueDic(query),
								options)).execute().body()).getCount();
	}

	public JsonNode findOne(String collectionName, JsonNode query, FindOptions options) throws IOException {
		return HiveResponseBody.KeyValueDict2JsonNode(
				HiveResponseBody.validateBody(
						databaseAPI.findOne(new FindDocRequestBody(collectionName,
								HiveResponseBody.jsonNode2KeyValueDic(query),
								options)).execute().body()).getItem());
	}

	public List<JsonNode> findMany(String collection, JsonNode query, FindOptions options) throws IOException {
		return HiveResponseBody.KeyValueDictList2JsonNodeList(
				HiveResponseBody.validateBody(
						databaseAPI.findMany(new FindDocsRequestBody(collection,
								HiveResponseBody.jsonNode2KeyValueDic(query),
								options))
								.execute()
								.body()).getItems());
	}
}
