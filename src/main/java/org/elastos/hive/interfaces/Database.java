package org.elastos.hive.interfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Database {


    /**
     * @param collection
     * @param schema
     */
    CompletableFuture<Void> createCol(String collection, String schema)
            throws Exception;

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
    CompletableFuture<String> insert(String collection, String item);

    /**
     * @param collection
     * @param params
     * @return
     * @throws Exception
     */
    CompletableFuture<String> query(String collection, String params);

    /**
     *
     * @param _id
     * @param etag
     * @return
     * @throws Exception
     */
    CompletableFuture<String> put(String collection, String _id, String etag, String item);

    /**
     *
     * @param _id
     * @param etag
     * @return
     * @throws Exception
     */
    CompletableFuture<String> patch(String collection, String _id, String etag, String item);

    /**
     *
     * @param _id
     * @param etag
     * @return
     * @throws Exception
     */
    CompletableFuture<String> delete(String collection, String _id, String etag);
}
