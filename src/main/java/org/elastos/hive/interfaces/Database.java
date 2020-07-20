package org.elastos.hive.interfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Database {

    /**
     *
     * @param collection
     * @param schema
     */
    CompletableFuture<Void> createCol(String collection, String schema)
            throws Exception;

    /**
     *
     * @param collection
     */
    CompletableFuture<Void> dropCol(String collection)
            throws Exception;

    /**
     *
     * @param collection
     * @param id
     * @return
     */
    CompletableFuture<Map<String, Object>> queryByID(String collection, String id)
            throws Exception;

    /**
     *
     * @param collection
     * @return
     */
    CompletableFuture<List<Map<String, Object>>> queryAll(String collection)
            throws Exception;

    /**
     *
     * @param collection
     * @param doc
     * @return
     * @throws Exception
     */
    CompletableFuture<Boolean> insert(String collection, String doc)
            throws Exception;

    /**
     *
     * @param table
     * @param oldDoc
     * @param newDoc
     * @return
     * @throws Exception
     */
    CompletableFuture<Boolean> update(String table, String oldDoc,
                                      String newDoc)
            throws Exception;

}
