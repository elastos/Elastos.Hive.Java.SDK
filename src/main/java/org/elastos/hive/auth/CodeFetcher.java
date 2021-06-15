package org.elastos.hive.auth;

import org.elastos.hive.connection.NodeRPCException;

public interface CodeFetcher {
	String fetch() throws NodeRPCException;
	void invalidate();
}
