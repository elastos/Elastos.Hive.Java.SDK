package org.elastos.hive.interfaces;

import java.util.concurrent.CompletableFuture;

public interface Database {

    /**
     * @param collection
     * @param schema
     */
    CompletableFuture<Void> createCol(String collection, String schema);

    /**
     * @param collection
     */
    CompletableFuture<Void> dropCol(String collection);

    /**
     * @param collection
     * @param item
     * @return
     * @throws Exception
     */
    CompletableFuture<String> post(String collection, String item);

    /**
     * @param collection
     * @param params
     * @return
     * @throws Exception
     */
    CompletableFuture<String> get(String collection, String params);

    /**
     *
     * @param _id
     * @param etag
     * @see <a href="https://docs.python-eve.org/en/stable/features.html#full-range-of-crud-operations">Data Integrity and Concurrency Control</a>
     * @return
     * @throws Exception
     */
    CompletableFuture<String> put(String collection, String _id, String etag, String item);

    /**
     *
     * @param _id
     * @param etag
     * @see <a href="https://docs.python-eve.org/en/stable/features.html#full-range-of-crud-operations">Data Integrity and Concurrency Control</a>
     * @return
     * @throws Exception
     */
    CompletableFuture<String> patch(String collection, String _id, String etag, String item);

    /**
     *
     * @param _id
     * @param etag
     * @see <a href="https://docs.python-eve.org/en/stable/features.html#full-range-of-crud-operations">Data Integrity and Concurrency Control</a>
     * @return
     * @throws Exception
     */
    CompletableFuture<String> delete(String collection, String _id, String etag);

}
