package org.elastos.hive.vault;

import org.elastos.hive.network.response.HiveResponseBody;

public interface HttpExceptionHandler {
    /**
     * Common exception conversion for response code.
     * Original exception comes from:
     *  1.class RequestInterceptor when handling response code.
     *  2.other sync/async logic.
     * Every service can override this for defining more specific ones.
     * @param e exception from http calling.
     * @return expect exception already defined.
     */
    default Exception convertException(Exception e) {
        return HiveResponseBody.convertException(e);
    }
}
