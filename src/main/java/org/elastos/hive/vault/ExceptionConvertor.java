package org.elastos.hive.vault;

import org.elastos.hive.connection.HiveResponseBody;

public interface ExceptionConvertor {
    /**
     * Common exception conversion for response code.
     * Original exception comes from:
     *  1.class RequestInterceptor when handling response code.
     *  2.other sync/async logic.
     * Every service can override this for defining more specific ones.
     * @param e exception from http calling.
     * @return expect exception already defined.
     */


	//
    default Exception toHiveException(Exception e) {
        return HiveResponseBody.convertException(e);
    }
}
