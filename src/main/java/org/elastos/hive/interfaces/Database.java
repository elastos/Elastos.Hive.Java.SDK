package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;

import java.util.concurrent.CompletableFuture;

public interface Database {

    /**
     * @param collection
     * @param schema
     */
    CompletableFuture<Void> createCol(String collection, String schema);

    CompletableFuture<Void> createCol(String collection, String schema, Callback<Long> callback);

    /**
     * @param collection
     */
    CompletableFuture<Void> dropCol(String collection);

    CompletableFuture<Void> dropCol(String collection, Callback<Long> callback);

    /**
     * @param collection
     * @param item
     * @return
     * @throws Exception
     */
    CompletableFuture<String> insert(String collection, String item);

    CompletableFuture<String> insert(String collection, String item, Callback<Long> callback);

    /**
     * @param collection
     * @param where
     * @return
     * @throws Exception
     */
    CompletableFuture<String> query(String collection, String where);

    CompletableFuture<String> query(String collection, String where, Callback<Long> callback);

    /**
     *
     * @param _id
     * @param etag
     * @see <a href="https://docs.python-eve.org/en/stable/features.html#full-range-of-crud-operations">Data Integrity and Concurrency Control</a>
     * @return
     * @throws Exception
     */
    CompletableFuture<String> put(String collection, String _id, String etag, String item);

    CompletableFuture<String> put(String collection, String _id, String etag, String item, Callback<Long> callback);

    /**
     *
     * @param _id
     * @param etag
     * @see <a href="https://docs.python-eve.org/en/stable/features.html#full-range-of-crud-operations">Data Integrity and Concurrency Control</a>
     * @return
     * @throws Exception
     */
    CompletableFuture<String> patch(String collection, String _id, String etag, String item);

    CompletableFuture<String> patch(String collection, String _id, String etag, String item, Callback<Long> callback);

    /**
     *
     * @param _id
     * @param etag
     * @see <a href="https://docs.python-eve.org/en/stable/features.html#full-range-of-crud-operations">Data Integrity and Concurrency Control</a>
     * @return
     * @throws Exception
     */
    CompletableFuture<String> delete(String collection, String _id, String etag);

    CompletableFuture<String> delete(String collection, String _id, String etag, Callback<Long> callback);

}
