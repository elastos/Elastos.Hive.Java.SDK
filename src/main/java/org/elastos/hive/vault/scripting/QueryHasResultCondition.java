package org.elastos.hive.vault.scripting;

import org.elastos.hive.connection.KeyValueDict;

/**
 * Vault script condition to check if a database query returns results or not.
 * This is a way for example to check if a user is in a group, if a message contains comments, if a user
 * is in a list, etc.
 */
public class QueryHasResultCondition extends Condition {
    private static final String TYPE = "queryHasResults";

    public QueryHasResultCondition(String name, String collectionName, KeyValueDict filter, Options options) {
        super(name, TYPE, null);
        super.setBody(new Body(collectionName, filter, options));
    }

    public QueryHasResultCondition(String name, String collectionName, KeyValueDict filter) {
        this(name, collectionName, filter, null);
    }

    public class Options {
        private Integer skip;
        private Integer limit;
        private Integer maxTimeMS;

        public Options(Integer skip, Integer limit, Integer maxTimeMS) {
            this.skip = skip;
            this.limit = limit;
            this.maxTimeMS = maxTimeMS;
        }
    }

    private class Body {
        private String collection;
        private KeyValueDict filter;
        private Options options;

        public Body(String collectionName, KeyValueDict filter, Options options) {
            this.collection = collectionName;
            this.filter = filter;
            this.options = options;
        }
    }
}
